package com.springboot.common.constants;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ResultDTO {

	private String status;
	private String msg;
	private String data;
	private String errorCode;
	
}
