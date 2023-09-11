package com.poly.controller.admin;

import java.io.IOException;

import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.poly.constant.SessionAttr;
import com.poly.entity.User;
import com.poly.service.UserService;
import com.poly.service.impl.UserServiceImpl;

@WebServlet(urlPatterns = { "/admin/user" }, name = "UserControllerOfAdmin")
public class UserController extends HttpServlet {

	private static final long serialVersionUID = -4914600244796962908L;
	
	private UserService userService = new UserServiceImpl();

	// GET
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User currentUser = (User) session.getAttribute(SessionAttr.CURENT_USER);

//		if (currentUser != null && currentUser.getIsAdnmin() == Boolean.TRUE) {
			String action = req.getParameter("action");

			switch (action) {
			case "view":
				doGetOverView(req, resp);
				break;
			case "delete":
				doGetDelete(req, resp);
				break;
			case "add":
				req.setAttribute("isEdit", false);
				doGetAdd(req, resp);
				break;
			case "edit":
				req.setAttribute("isEdit", true);
				doGetEdit(req, resp);
				break;
			}
//		} else {
//			resp.sendRedirect("index");
//		}
	}

	// localhost:8080/java4asm/admin/user?action=view
	private void doGetOverView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<User> users = userService.findAll();
		req.setAttribute("users", users);
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/admin/user-overview.jsp");
		requestDispatcher.forward(req, resp);
	}

	// localhost:8080/java4asm/admin/user?action=delete&username={username}
	private void doGetDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String username = req.getParameter("username");
		// delete = update >>> setIsactive = FALSE
		User userDeleted = userService.delete(username);

		if (userDeleted != null) { // xoa thanh cong
			resp.setStatus(204);
		} else {
			resp.setStatus(400);
		}
	}

	// localhost:8080/java4asm/admin/user?action=add
	private void doGetAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/admin/user-edit.jsp");
		requestDispatcher.forward(req, resp);
	}

	// localhost:8080/java4asm/admin/user?action=edit&username={username}
	private void doGetEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		User user = userService.findByUsername(username);

		req.setAttribute("user", user);
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/admin/user-edit.jsp");
		requestDispatcher.forward(req, resp);
	}

	// POST
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User currentUser = (User) session.getAttribute(SessionAttr.CURENT_USER);

//		if (currentUser != null && currentUser.getIsAdnmin() == Boolean.TRUE) {
			String action = req.getParameter("action");

			switch (action) {
			case "add":
				doPostAdd(req, resp);
				break;
			case "edit":
				doPostEdit(req, resp);
				break;
			}
//		} else {
//			resp.sendRedirect("index");
//		}
	}

	private void doPostAdd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String username = req.getParameter("newusername");
		String password = req.getParameter("password");
		String email = req.getParameter("email");

		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);

		User userReturn = userService.register(username, password, email);
		if (userReturn != null) {
			resp.setStatus(204);
		} else {
			resp.setStatus(400);
		}
	}

	private void doPostEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String username = req.getParameter("newusername");
		String password = req.getParameter("password");
		String email = req.getParameter("email");
		String usernameOrigin = req.getParameter("usernameOrigin");
		
		User user = userService.findByUsername(usernameOrigin);
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);

		User userReturn = userService.update(user);
		if (userReturn != null) {
			resp.setStatus(204);
		} else {
			resp.setStatus(400);
		}
	}
}
