package com.nicu.httpd.handlers;

import org.json.JSONObject;

import com.nicu.httpd.NanoHTTPD.IHTTPSession;
import com.nicu.httpd.NanoHTTPD.Method;
import com.nicu.httpd.NanoHTTPD.Response;
import com.nicu.httpd.NanoHTTPD.Response.Status;
import com.nicu.model.Robot;

public class UpdateHandler extends RequestHandler {

	public UpdateHandler() {
		super("/robot/update");
	}

	@Override
	public Response handleRequest(IHTTPSession session) {
		if (Method.PUT.equals(session.getMethod())) {
			try {
				Integer contentLength = Integer.parseInt(session.getHeaders().get("content-length"));
				byte[] buf = new byte[contentLength];
			    session.getInputStream().read(buf, 0, contentLength);

			    String body = new String(buf);
				JSONObject json = new JSONObject(body);
				Robot.getInstance().fromJSON(json);
				
				return new Response(Robot.getInstance().toString());
			} catch (Exception e) {
				e.printStackTrace();
				return new Response(Status.INTERNAL_ERROR, RESPONSE_MIME_PLAINTEXT, e.getMessage());
			}
		}
		
		return null;
	}

}
