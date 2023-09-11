package com.poly.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.poly.constant.SessionAttr;
import com.poly.entity.History;
import com.poly.entity.User;
import com.poly.entity.Video;
import com.poly.service.HistoryService;
import com.poly.service.VideoService;
import com.poly.service.impl.HistoryServiceImpl;
import com.poly.service.impl.VideoServiceImpl;


@WebServlet(urlPatterns = { "/index", "/favorites", "/history" })
public class HomeController extends HttpServlet {

	private static final long serialVersionUID = -5503270555298699564L;

	private static final int VIDEO_MAX_PAGE_SIZE = 4;
	
	private VideoService videoService = new VideoServiceImpl();
	
	private HistoryService historyService = new HistoryServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		
		String path = req.getServletPath(); 

		switch (path) {
		case "/index":
			doGetIndex(req, resp);
			break;
		case "/favorites":
			doGetFavorites(session, req, resp);
			break;
		case "/history":
			doGetHistory(session, req, resp);
			break;
		}
		
	}
	
	//http://localhost:8080/java4asm/index?page={page}
	protected void doGetIndex(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {		
		//tính maxPage = tổng số video / maxPageSize (chia lẻ làm tròn)
				//10 video, muốn chia mỗi trang có 4 video >>> có tổng cộng 10/4 = 2.5 >> 3 trang
		List<Video> countVideo = videoService.findAll();
		
		//Math.ceil làm tròn thành double >> chuyển sang (int) 
		//(double)VIDEO_MAX_PAGE_SIZE vì vế đầu đã (int) thì khi chia với (int) sẽ ra (double) >> chuyển vế sau thành (double) >> int / double >> int
		int maxPage = (int) Math.ceil(countVideo.size() / (double)VIDEO_MAX_PAGE_SIZE); 
		req.setAttribute("maxPage", maxPage);	
		
		List<Video> videos;
		String pageNumber = req.getParameter("page");	
		
		//pageNumber, pageSize(tự định nghĩa) //phân trang
		if (pageNumber == null || Integer.valueOf(pageNumber) > maxPage) { //nếu phaeNumber null or quá số page >> quay về page 1
			videos = videoService.findAll(1, VIDEO_MAX_PAGE_SIZE); //k truyền pageNumber, vào trang index đầu tiên -> page 1
			req.setAttribute("currentPage", 1);
		} else {
			videos = videoService.findAll(Integer.valueOf(pageNumber), VIDEO_MAX_PAGE_SIZE);
			req.setAttribute("currentPage", Integer.valueOf(pageNumber));
		}
		req.setAttribute("videos", videos);
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/user/index.jsp");
		requestDispatcher.forward(req, resp);
	}
	
	protected void doGetFavorites(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = (User) session.getAttribute(SessionAttr.CURENT_USER);
		List<History> histories = historyService.findByUserAndIsLiked(user.getUsername());
		List<Video> videos = new ArrayList<>();
		histories.forEach(item -> videos.add(item.getVideo())); //java 8
		/*
		 * for (int i = 0; i < histories.size(); i++) {
		 * 		videos.add(histories.get(i).getVideo());
		 * }
		 */
		req.setAttribute("videos", videos);
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/user/favorites.jsp");
		requestDispatcher.forward(req, resp);
	}
	
	protected void doGetHistory(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = (User) session.getAttribute(SessionAttr.CURENT_USER);
		List<History> histories = historyService.findByUser(user.getUsername());
		List<Video> videos = new ArrayList<>();
		histories.forEach(item -> videos.add(item.getVideo())); //java 8

		req.setAttribute("videos", videos);
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/user/history.jsp");
		requestDispatcher.forward(req, resp);
	}
}