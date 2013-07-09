package net.archmage.house.beagle.gpio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GpioService {

	public static final String GPIO_BASE = "/sys/class/gpio";

	public static final String GPIO_EXPORT = "/export";

	private static final String GPIO_UNEXPORT = "/unexport";

	public static final String GPIO_DIRECTION = "/direction";

	public static final String GPIO_DIRECTION_OUT_HIGH = "high";

	public static final String GPIO = "/gpio";

	public static final String GPIO1_28 = "60";

	public static final String GPIO_VALUE = "/value";

	public static GpioService gpioService = null;

	public static GpioService getInstance() {
		if (gpioService == null) {
			synchronized (GPIO) {
				if (gpioService == null) {
					gpioService = new GpioService();
					gpioService.initGpio();
				}
			}
		}
		return gpioService;
	}

	protected GpioService() {
		super();
	}

	public void execute() throws Exception {
		initGpio();
		switchOn(GPIO1_28);
		Thread.sleep(500);
		switchOff(GPIO1_28);
		Thread.sleep(500);
		switchOn(GPIO1_28);
		Thread.sleep(500);
		switchOff(GPIO1_28);
	}

	private void initGpio() {
		initGpioExport();
		initGpioDirection();
	}

	public void releaseGpio() {
		releaseGpioExport();
	}

	private void releaseGpioExport() {
		unexport(GPIO1_28);
	}

	private void initGpioExport() {
		unexport(GPIO1_28);
		export(GPIO1_28);
	}

	private void unexport(String gpio) {
		File gpioDir = new File(GPIO_BASE + GPIO + gpio);
		if (gpioDir.exists()) {
			File f = new File(GPIO_BASE + GPIO_UNEXPORT);
			writeToFile(f, gpio);
		}
	}

	private void export(String gpio) {
		File f = new File(GPIO_BASE + GPIO_EXPORT);
		writeToFile(f, gpio);
	}

	private void initGpioDirection() {
		File f = new File(GPIO_BASE + GPIO + GPIO1_28 + GPIO_DIRECTION);
		writeToFile(f, GPIO_DIRECTION_OUT_HIGH);
	}

	public void switchOn(String gpio) {
		// System.out.println("switching on");
		switchGpio(gpio, true);
	}

	public void switchOff(String gpio) {
		// System.out.println("switching off");
		switchGpio(gpio, false);
	}

	public void switchGpio(String gpio, boolean on) {
		String value = "0";
		if (on) {
			value = "1";
		}
		File f = new File(GPIO_BASE + GPIO + gpio + GPIO_VALUE);
		writeToFile(f, value);
	}

	private void writeToFile(File file, String content) {
		// System.out.println("writing '" + content + "' to file:" + file);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(content);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e1) {
				}
			}
		}
	}

}
