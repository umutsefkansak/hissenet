package com.infina.hissenet.dto.request;

public record AccountPasswordChangeRequest(
		String newPassword,
		String confirmNewPassword
) {

}
