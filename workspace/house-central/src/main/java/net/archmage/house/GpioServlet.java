package net.archmage.house;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.archmage.house.beagle.gpio.GpioService;

public class GpioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String SERVLET_URL = "/gpio/";
	
	private static final String ON = "on";

	private static final String OFF = "off";
	
	private GpioService gpioService = null;
	
	@Override
	public void init() {
		gpioService = GpioService.getInstance();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String rest = req.getRequestURI().substring(req.getContextPath().length() + SERVLET_URL.length());
		System.out.println("rest-uri=" + rest);
		
		if (rest.equals(ON)) {
			gpioService.switchOn(GpioService.GPIO1_28);
		} else if (rest.equals(OFF)) {
			gpioService.switchOff(GpioService.GPIO1_28);
		}
	}

	@Override
	public void destroy() {
		if (gpioService != null) {
			gpioService.releaseGpio();
			gpioService = null;
		}
	}
	
}
