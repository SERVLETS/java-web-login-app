import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/submit")
public class TokenGenerationServlet extends HttpServlet {

	final String cookieName = "my_app_ck";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub

		String str = getClientUrl(req);

		String uname = req.getParameter("username").toLowerCase();
		manageCookie(req, resp);
		// forward("index.jsp", req, resp);

		redirect(str, resp);
	}

	private String getClientUrl(HttpServletRequest req) {
		Cookie ck = getCookie(req, "clientUrl");
		if (ck == null) {
			throw new RuntimeException("Invalid client url");
		}
		String clientUrl = ck.getValue();
		return clientUrl;
	}

	public Cookie getCookie(HttpServletRequest req, String findCookieName) {
		Cookie[] cookies = req.getCookies();
		Cookie ck = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(findCookieName)) {
					ck = cookie;
					break;
				}
			}
		}

		return ck;
	}

	public void manageCookie(HttpServletRequest req, HttpServletResponse resp) {

		Cookie cookie = getCookie(req, cookieName);

		if (cookie != null) {
			String[] cookieArr = cookie.getValue().split(":");
			long cookieTime = Long.valueOf(cookieArr[1]);
			long timeLapsed = System.currentTimeMillis() - cookieTime;
			System.out.println("time: " + timeLapsed);
			if (timeLapsed > 10 * 1000) {
				System.out.println("time limit expired for old cookie. generate new cookie");
				resp.addCookie(createCookie());
			} else {
				System.out.println("we got a valid cookie. no need to do anything");
			}
		}

		else {
			System.out.println("creating a new cookie");
			resp.addCookie(createCookie());
		}
	}

	public Cookie createCookie() {

		final String cookieValue = "c1:" + System.currentTimeMillis(); // you
																		// could
																		// assign
																		// it
																		// some
																		// encoded
		// value
		final Boolean useSecureCookie = false;
		final int expiryTime = 60 * 5; // 24h in seconds
		final String cookiePath = "/";

		Cookie cookie = new Cookie(cookieName, cookieValue);

		cookie.setSecure(useSecureCookie); // determines whether the cookie
											// should only be sent using a
											// secure protocol, such as HTTPS or
											// SSL

		cookie.setMaxAge(expiryTime); // A negative value means that the cookie
										// is not stored persistently and will
										// be deleted when the Web browser
										// exits. A zero value causes the cookie
										// to be deleted.

		// cookie.setPath(cookiePath); // The cookie is visible to all the pages
		// in the directory you specify, and all the pages in that directory's
		// subdirectories

		return cookie;
	}

	public void forward(String str, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		RequestDispatcher rd = req.getRequestDispatcher(str);
		rd.forward(req, resp);
	}

	public void redirect(String str, HttpServletResponse resp) throws IOException {
		System.out.println("redirecting: "+str);
		resp.sendRedirect(str);
	}

}
