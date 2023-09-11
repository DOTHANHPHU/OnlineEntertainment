package com.poly.service.impl;

import java.sql.Timestamp;
import java.util.List;

import com.poly.dao.HistoryDao;
import com.poly.dao.impl.HistoryDaoImpl;
import com.poly.entity.History;
import com.poly.entity.User;
import com.poly.entity.Video;
import com.poly.service.HistoryService;
import com.poly.service.VideoService;

public class HistoryServiceImpl implements HistoryService{

	private HistoryDao dao;
	private VideoService videoService = new VideoServiceImpl();
	
	public HistoryServiceImpl() {
		dao = new HistoryDaoImpl();
	}
	
	@Override
	public List<History> findByUser(String username) {
		return dao.findByUser(username);
	}

	@Override
	public List<History> findByUserAndIsLiked(String username) {
		return dao.findByUserAndIsLiked(username);
	}

	@Override
	public History findByUserIdAndVideoId(Integer userId, Integer videoId) {
		return dao.findByUserIdAndVideoId(userId, videoId);
	}

	@Override
	public History create(User user, Video video) {
		History exisHistory = findByUserIdAndVideoId(user.getId(), video.getId());
		
		if (exisHistory == null) {
			exisHistory = new History();
			exisHistory.setUser(user);
			exisHistory.setVideo(video);
			exisHistory.setViewedDate(new Timestamp(System.currentTimeMillis()));
			exisHistory.setIsLiked(Boolean.FALSE);
			return dao.create(exisHistory);
		}
		return exisHistory;
	}

	@Override
	public boolean updateLikeOrUnlike(User user, String videoHref) {
		Video video = videoService.findByHref(videoHref);
		History exisHistory = findByUserIdAndVideoId(user.getId(), video.getId());
		
		if(exisHistory.getIsLiked() == Boolean.FALSE) {
			exisHistory.setIsLiked(Boolean.TRUE);
			exisHistory.setLikedDate(new Timestamp(System.currentTimeMillis()));
		} else {
			exisHistory.setIsLiked(Boolean.FALSE);
			exisHistory.setLikedDate(null);
		}
		History updatedHistory = dao.update(exisHistory);
		return updatedHistory != null ? true : false; //toan tu 3 ngoi
	}

}
