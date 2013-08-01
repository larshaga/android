/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.digipost.android.constants;

public class ApiConstants {

	public static final String LOCATION_ARCHIVE = "ARCHIVE";
	public static final String LOCATION_WORKAREA = "WORKAREA";
	public static final String FILETYPE_PDF = "pdf";
	public static final String FILETYPE_HTML = "html";
	public static final String[] FILETYPES_IMAGE = { "jpg", "jpeg", "png" };

	public static final String GET_RECEIPT = "receipt";

	public static final String DELETE = "delete";
    public static final String REFRESH_ARCHIVE = "refreshArchive";
    public static final String LOGOUT = "logout";
	public static final String ACTION = "action";
	public static final String AUTHENTICATION_LEVEL_TWO_FACTOR = "TWO_FACTOR";
    public static final String INVOICE = "INVOICE";
	public static final String GRANT_TYPE = "grant_type";
	public static final String CODE = "code";
	public static final String RESPONSE_TYPE = "response_type";
	public static final String CLIENT_ID = "client_id";
	public static final String NONCE = "nonce";
	public static final String POST = "POST";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String REDIRECT_URI = "redirect_uri";
	public static final String STATE = "state";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String ACCEPT = "Accept";
	public static final String AUTHORIZATION = "Authorization";
	public static final String POST_API_ACCESSTOKEN_HTTP = "/post/api/oauth/accesstoken HTTP/1.1";
	public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String BEARER = "Bearer ";
	public static final String BASIC = "Basic ";
	public static final String APPLICATION_VND_DIGIPOST_V2_JSON = "application/vnd.digipost-v2+json";
	public static final String CONTENT_OCTET_STREAM = "application/octet-stream";
	public static final String TEXT_HTML = "text/html";

	public static final String URL = "https://www.digipost.no/post/";

	public static final String URL_API = URL + "api";
	public static final String URL_RELATIONS_DOCUMENT_INBOX = URL + "relations/document_inbox";
	public static final String URL_RELATIONS_DOCUMENT_ARCHIVE = URL + "relations/document_archive";
	public static final String URL_RELATIONS_DOCUMENT_KITCHENBENCH = URL + "relations/document_workarea";
	public static final String URL_RELATIONS_DOCUMENT_RECEIPTS = URL + "relations/receipts";
	public static final String URL_RELATIONS_DOCUMENT_GET_CONTENT = URL + "relations/get_document_content";
	public static final String URL_RELATIONS_DOCUMENT_SELF = URL + "relations/self";
	public static final String URL_RELATIONS_DOCUMENT_UPDATE = URL + "relations/update_document";
	public static final String URL_RELATIONS_DOCUMENT_DELETE = URL + "relations/delete_document";
	public static final String URL_RELATIONS_DOCUMENT_SEND_OPENING_RECEIPT = URL + "relations/send_opening_receipt";
	public static final String URL_RELATIONS_DOCUMENT_GET_ORGANIZATION_LOGO = URL + "relations/organisation_logo";
	public static final String URL_API_OAUTH_AUTHORIZE_NEW = URL + "api/oauth/authorize/new";
	public static final String URL_API_OAUTH_ACCESSTOKEN = URL + "api/oauth/accesstoken";
    public static final String URL_RELATIONS_CURRENT_BANK_ACCOUNT = URL + "relations/current_bank_account";
    public static final String URL_RELATIONS_BANK_HOMEPAGE = URL + "relations/bank_homepage";
    public static final String URL_RELATIONS_SEND_TO_BANK = URL + "relations/send_to_bank";

}