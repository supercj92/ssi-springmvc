package com.cfysu.ssi.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/file")
public class FileUploadDownloadController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadDownloadController.class);
	/**
	 * 涓婁紶鏂囦欢
	 * @return
	 */
	@RequestMapping("/toUpload")
	public String toUpload(){
		return "upload";
	}

	@RequestMapping("/upload")
	@ResponseBody
	public String uploadFile(@RequestParam(value = "Filedata") MultipartFile file){
		if(file == null){
			LOGGER.info("涓婁紶鏂囦欢涓虹┖");
			return "鏂囦欢涓虹┖";
		}
		LOGGER.info("fileName:{}", file.getOriginalFilename());
		URL url = FrontPageController.class.getClassLoader().getResource("/");
		try {
			file.transferTo(new File(url.getPath() + File.separator + file.getOriginalFilename()));
		} catch (IOException e) {
			e.printStackTrace();
			return "fail";
		}
		LOGGER.info("涓婁紶鎴愬姛");
		return "success";
	}
	
	@RequestMapping("/download")
	public ResponseEntity<byte[]> download(){
		URL url = FileUploadDownloadController.class.getClassLoader().getResource("/");
		File log4jFile = new File(url.getPath() + File.separator + "log4j.xml");
		LOGGER.info("filePath:{}", log4jFile.getAbsoluteFile());
		byte[] fileBytes = null;
		try {
			fileBytes = FileUtils.readFileToByteArray(log4jFile);
			LOGGER.info("鏂囦欢璇诲彇鎴愬姛");
		} catch (IOException e) {
			LOGGER.error("鏂囦欢杞瓧鑺傛暟缁勫紓甯�, e);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", log4jFile.getName());
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<byte[]>(fileBytes, headers, HttpStatus.CREATED);
	}
}
