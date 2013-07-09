package net.archmage.house.beagle;

import net.archmage.house.beagle.gpio.GpioService;

public class App {

	public static void main(String[] args) {
		try {
			GpioService.getInstance().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
