package com.it15136466.IT15136466.controller;

import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.it15136466.IT15136466.model.Account;
import com.it15136466.IT15136466.model.User;

@Controller
public class CookieController {

	//store cookies
	HashMap<String, String> cookieStore = new HashMap<>();

	//load login page
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "index";
	}

	//process login
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String submit(@Valid @ModelAttribute("User") User user, BindingResult result, Model model,
			HttpServletResponse response, HttpServletRequest request) {

		//check login credentials
		if (user.getUsername().equals("admin") && user.getPassword().equals("admin")) {
			if (result.hasErrors()) {
				return "error"; 
			}

			//generate random value for session cookie
			String ssid_value = generateRamdomValue();

			//store session cookie o browser
			Cookie c1 = new Cookie("ssid", ssid_value);
			c1.setMaxAge(600 * 600); // set expire time for 1 hour
			c1.setHttpOnly(true); //session cookie doesn't read by javascript
			c1.setSecure(false); // this example is not using https
			response.addCookie(c1);
			cookieStore.put("ssid", ssid_value);

			//store csrf cookie on browser
			Cookie c2 = new Cookie("csrf", generateRamdomValue());
			c2.setMaxAge(600 * 600); // set expire time for 1 hour
			c2.setHttpOnly(false); // this cookie read by javascript
			c2.setSecure(false); // this example is not using https
			response.addCookie(c2);

			//load user view page
			return "UserView";
		} else {
			//load error page for invalid logins
			return "error"; 
		}

	}

	//process transfer money
	@RequestMapping(value = "/doublecookie", method = RequestMethod.POST)
	public String transferMoneyDouble(@Valid @ModelAttribute("Account") Account account, BindingResult result,
			Model model, HttpServletResponse response, HttpServletRequest request) {

		String r_c1 = null, r_csrf = null;
		//get cookies from request
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				System.out.println("called " + cookie.getName());

				if (cookie.getName().equals("ssid")) {
					//get session cookie
					r_c1 = cookie.getValue();
				} else if (cookie.getName().equals("csrf")) {
					//get csrf cookie
					r_csrf = cookie.getValue();
				}

			}
		}

		//check session cookie and csrf cookie
		if (r_c1.equals(cookieStore.get("ssid")) && account.getCsrf().equals(r_csrf)) {

			//set message on user view page
			model.addAttribute("msg", "your money transfer is successfull");
			return "UserView";
		} else {

			return "error";
		}

	}

	//generate random string value for cookies
	protected String generateRamdomValue() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
	
}
