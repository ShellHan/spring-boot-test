package com.springboot.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @author: liuyafei
 * @date 创建时间：2017年3月27日
 * @version 1.0
 * @parameter
 * @return
 */
public class DateKit {
	/**
	 * 获取当前时间long
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月4日
	 * @version 1.0
	 * @parameter
	 * @return
	 */
	public static Long getNowLongTime() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 获取当前日的天
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月11日
	 * @version 1.0
	 * @parameter
	 * @return
	 */
	public static int getNowDay() {
		Calendar cale = Calendar.getInstance();
		return cale.get(Calendar.DATE);
	}
	
	/**
	 * 获取当前日的小时
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月11日
	 * @version 1.0
	 * @parameter
	 * @return
	 */
	public static int getNowHour() {
		Calendar cale = Calendar.getInstance();
		return cale.get(Calendar.HOUR_OF_DAY);
	}
	
	
	/**
	 * 获取的天
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月11日
	 * @version 1.0
	 * @parameter
	 * @return
	 * @throws ParseException 
	 */
	public static Date getDateByYMD(String time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return formatter.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		return new Date();
	}

	/**
	 * 判断时间是否过期
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月4日
	 * @version 1.0
	 * @parameter
	 * @return true 过期 false 未过期
	 */
	public static boolean compareTime(String oldTime) {

		return getNowLongTime() >= Long.parseLong(oldTime);
	}

	/**
	 * 格式化时间-yyyy-MM-dd HH:mm:ss
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月15日
	 * @version 1.0
	 * @parameter
	 * @return
	 */
	public static String getFmtDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(date);
	}
	/**
	 * 格式化时间-yyyy-MM-dd
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月15日
	 * @version 1.0
	 * @parameter
	 * @return
	 */
	public static String getFmtYMDDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
	/**
	 * 格式化时间-dd日HH时mm分
	 * 
	 * @author: liuyafei
	 * @date 创建时间：2017年5月15日
	 * @version 1.0
	 * @parameter
	 * @return
	 */
	public static String getFmtDHMDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd日HH时mm分");
		return formatter.format(date);
	}
	/**
	 * 操作时间天  day为-1 则提前一天 1 为推后一天
	* @author: liuyafei
	* @date 创建时间：2017年5月19日
	* @version 1.0 
	* @parameter  
	* @return
	 */
	public static Date optCalendarDay(Date date,int day){
		Calendar cale = Calendar.getInstance(); 
		cale.setTime(date);
		cale.add(Calendar.DAY_OF_MONTH, day);
		return cale.getTime();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 10; i++) {
			System.out.println(DateKit.getNowHour());
			System.out.println(DateKit.getNowDay());
		}
	}

}
