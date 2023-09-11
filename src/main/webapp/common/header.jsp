<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglib.jsp" %>
<!-- Page Loader -->
<div id="loader-wrapper">
	<div id="loader"></div>

	<div class="loader-section section-left"></div>
	<div class="loader-section section-right"></div>

</div>
<nav class="navbar navbar-expand-lg">
	<div class="container-fluid">
		<a class="navbar-brand" href="<c:url value='/index'/>"> <i class="fas fa-film mr-2"></i>
			Online Entertainment
		</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarSupportedContent"
			aria-controls="navbarSupportedContent" aria-expanded="false"
			aria-label="Toggle navigation">
			<i class="fas fa-bars"></i>
		</button>
		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav ml-auto mb-2 mb-lg-0">

				<c:choose>
					<c:when test="${not empty sessionScope.currentUser}">
						<li class="nav-item">
							<a class="nav-link nav-link-1 active"
								aria-current="page" data-toggle="modal"
								data-target="#changePassModal">Welcome, ${sessionScope.currentUser.username}
							</a>
						</li>
						<li class="nav-item"><a class="nav-link nav-link-2"
							href="favorites">My favorite</a></li>
						<li class="nav-item"><a id="historyBtn" class="nav-link nav-link-3"
							href="history">History</a></li>
						<li class="nav-item"><a class="nav-link nav-link-4"
							href="logout">Logout</a></li>
					</c:when>

					<c:otherwise>
						<li class="nav-item"><a class="nav-link nav-link-1"
							href="login">Login</a></li>
						<li class="nav-item"><a class="nav-link nav-link-2"
							href="register">Register</a></li>
						<li class="nav-item"><a class="nav-link nav-link-3"
							href="forgotPass">Forgot password</a></li>
					</c:otherwise>
				</c:choose>

			</ul>
		</div>
	</div>
	
	<!-- Modal -->
	<div class="modal fade" id="changePassModal" tabindex="-1"
		aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">Change Password</h5>
					<button type="button" class="btn-close" data-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<input id="currentPass" type="password" name="currentPass"
							class="form-control rounded-0" placeholder="Current Password"
							required />
					</div>
	
					<div class="form-group">
						<input id="newPass" type="password" name="newPass"
							class="form-control rounded-0" placeholder="New Password"
							required />
					</div>
					<h6 id="messageChangePass" style="color: red"></h6>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-warning"
						data-dismiss="modal">Close</button>
					<button id="changePassBtn" type="button" class="btn btn-warning">Save changes</button>
				</div>
			</div>
		</div>
	</div>
</nav>

<div class="tm-hero d-flex justify-content-center align-items-center"
	data-parallax="scroll"
	data-image-src="<c:url value = '/templates/user/img/hero.jpg'/>">
	<form class="d-flex tm-search-form">
		<input class="form-control tm-search-input" type="search"
			placeholder="Search" aria-label="Search">
		<button class="btn btn-outline-success tm-search-btn" type="submit">
			<i class="fas fa-search"></i>
		</button>
	</form>
</div>

	

