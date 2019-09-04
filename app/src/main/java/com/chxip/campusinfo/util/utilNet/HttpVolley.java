package com.chxip.campusinfo.util.utilNet;

import android.os.Handler;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.chxip.campusinfo.application.MyApplication;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用volley框架进行http网络访问
 */
public class HttpVolley {

	// 获取RequestQueue对象
	public static RequestQueue requestQueue = MyApplication.getRequestQueue();

	/**
	 * volley 发送StringRequest登录
	 *
	 * @param url
	 *            访问路径
	 * @param map
	 *            参数
	 * @param ncbv
	 *            网络访问完成后 处理数据的接口
	 */
	public static void volleStringRequestPostLogin(String url, final HashMap<String, String> map, NetCallBackVolley ncbv) {
		StringRequest request = new StringRequest(Request.Method.POST, url, ncbv, ncbv) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (map == null) {
					return null;
				}
				return map;
			}
		};
		request.setTag(url);
		request.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);

	}


	/**
	 * volley 发送StringRequest
	 *
	 * @param url
	 *            访问路径
	 * @param ncbv
	 *            网络访问完成后 处理数据的接口
	 */
	public static void volleStringRequestGetString(String url, NetCallBackVolley ncbv, Handler handler) {

		try {
			url=URLEncoderURI.encode(url,"UTF-8");
			url=url.replace(" ","%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringRequest request = new StringRequest(Method.GET, url, ncbv, ncbv) {
			@Override
			public String getBodyContentType() {
				// 请求参数x-www-form-urlencoded
				return String.format("application/x-www-form-urlencoded; charset=%s", "utf-8");
			}
			/**
			 * 重写此方法不会导致乱码
			 */
			@Override
			public Response<String> parseNetworkResponse(
					NetworkResponse response) {
				try {
					String jsonString = new String(response.data, "UTF-8");
					return Response.success(jsonString,HttpHeaderParser.parseCacheHeaders(response));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					return Response.error(new ParseError(e));
				}

			}
		};
		request.setTag(url);

		request.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		requestQueue.add(request);
	}

	/**
	 * volley 发送StringRequest POST请求 请求参数类型x-www-form-urlencoded
	 *
	 * @param url
	 *            访问路径
	 * @param map
	 *            参数
	 * @param ncbv
	 *            网络访问完成后 处理数据的接口
	 */
	public static void volleStringRequestPost(String url, final HashMap<String, String> map, NetCallBackVolley ncbv) {
		try {
			url=URLEncoderURI.encode(url,"UTF-8");
			url=url.replace(" ","%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		StringRequest request = new StringRequest(Request.Method.POST, url, ncbv, ncbv) {

			@Override
			public String getBodyContentType() {
				// 请求参数x-www-form-urlencoded
				return String.format("application/x-www-form-urlencoded; charset=%s", "utf-8");
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (map == null) {
					return null;
				}
				return map;
			}
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return null;

			}
		};
		request.setTag(url);
		request.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);

	}

	/**
	 * volley 发送StringRequest POST请求  请求参数类型json  返回JSON
	 *
	 * @param url
	 *            访问路径
	 */
	public static void volleStringRequestPostJson(String url, JSONObject jsonObject,ListenerJSONObject lj) {
		try {
			url=URLEncoderURI.encode(url,"UTF-8");
			url=url.replace(" ","%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Method.POST,url, jsonObject,lj, lj)
		{
			@Override
			public String getBodyContentType() {
				// 请求参数x-www-form-urlencoded
				return String.format("application/json; charset=%s", "utf-8");
			}
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {

				return null;

			}


		};
		jsonRequest.setTag(url);
		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(jsonRequest);

	}



	/**
	 * 取消连接
	 *
	 * @param tag
	 *            连接的TAG
	 */
	public static void cancelConnect(String tag) {
		requestQueue.cancelAll(tag);
	}

	/**
	 * 进行网络连接 此方法会判断验证码是否过期，过期了提示从新登录
	 * 参数为Map
	 */
	public static void sendNetworkConnection(String url, HashMap<String, String> map, NetCallBackVolley ncbv,Handler handler) {
		volleStringRequestPost(url, map, ncbv);
	}

	/**
	 * 进行网络连接 此方法会判断验证码是否过期，过期了会从新获取
	 * 参数为json
	 */
	public static void sendNetworkConnectionJson(String url, JSONObject jsonObject,ListenerJSONObject lj,Handler handler) {
		volleStringRequestPostJson(url, jsonObject,lj);
	}

	/**
	 * volley 发送StringRequest
	 *
	 * @param url
	 *            访问路径
	 * @param ncbv
	 *            网络访问完成后 处理数据的接口
	 */
	public static void volleStringRequestGetStringOne(String url, NetCallBackVolley ncbv, Handler handler) {
		StringRequest request = new StringRequest(Method.GET, url, ncbv, ncbv) {
			@Override
			public String getBodyContentType() {
				// 请求参数x-www-form-urlencoded
				return String.format("application/x-www-form-urlencoded; charset=%s", "utf-8");
			}

		};

		request.setTag(url);
		request.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);

	}

	/**
	 * volley 发送StringRequest POST请求 请求参数类型x-www-form-urlencoded
	 *
	 * @param url
	 *            访问路径
	 * @param map
	 *            参数
	 * @param ncbv
	 *            网络访问完成后 处理数据的接口
	 */
	public static void volleStringRequestPostOne(String url, final HashMap<String, String> map, NetCallBackVolley ncbv) {
		StringRequest request = new StringRequest(Request.Method.POST, url, ncbv, ncbv) {

			@Override
			public String getBodyContentType() {
				// 请求参数x-www-form-urlencoded
				return String.format("application/x-www-form-urlencoded; charset=%s", "utf-8");
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (map == null) {
					return null;
				}
				return map;
			}

		};
		request.setTag(url);
		request.setRetryPolicy(new DefaultRetryPolicy(5000, // 默认超时时间
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // 默认最大尝试次数
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);

	}

}
