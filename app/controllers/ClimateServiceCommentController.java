/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.math.BigInteger;
import play.libs.Json;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import models.*;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

@Named
@Singleton
public class ClimateServiceCommentController extends Controller {
	private final ClimateServiceCommentRepository climateServiceCommentRepository;
	private final ClimateServiceRepository climateServiceRepository;
	private final HashTagRepository hashTagRepository;
	private final UserRepository userRepository;
	private final MentionRepository mentionRepository;

	private final Pattern HASHTAG_PATTERN = Pattern.compile("#(\\S+)");
	private final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Inject
	public ClimateServiceCommentController(ClimateServiceCommentRepository climateServiceCommentRepository,
										   ClimateServiceRepository climateServiceRepository,
										   HashTagRepository hashTagRepository, UserRepository userRepository,
										   MentionRepository mentionRepository) {
		this.climateServiceCommentRepository = climateServiceCommentRepository;
		this.climateServiceRepository = climateServiceRepository;
		this.hashTagRepository = hashTagRepository;
		this.userRepository = userRepository;
		this.mentionRepository = mentionRepository;
	}

	private String failJson(String msg) {
		ObjectNode response = Json.newObject();
		response.put("success", false);
		response.put("message", msg);

		return response.toString();
	}

	private String checkMention(String text) {
		List<String> usernames = userRepository.getAllUserName();

		for (String username : usernames) {
			String mentionUsername = '@' + username + ' ';
			int index = text.indexOf(mentionUsername);

			if (index >= 0) {
				User user = userRepository.findByUsername(username);
				user.setUnreadMention(true);
				userRepository.save(user);

				String before = text.substring(0, index);
				String after = text.substring(index + username.length() + 1);
				text = before + "<b style=\"background-color: #59D0F7\">"
						+ username + "</b>" + after;
			}
		}

		return text;
	}

	private void saveMention(String text, Long commentId) {
		List<String> usernames = userRepository.getAllUserName();

		for (String username : usernames) {
			String mentionUsername = '@' + username + ' ';
			int index = text.indexOf(mentionUsername);

			if (index >= 0) {
				Mention mention = new Mention(username, commentId);
				mentionRepository.save(mention);
			}
		}
	}

	private ArrayNode getCommentArray(Long elementId, Long parentId) {
		List<ClimateServiceComment> climateServiceComments = climateServiceCommentRepository
				.findAllByClimateServiceIdAndParentId(elementId, parentId);
		ArrayNode commentArray = JsonNodeFactory.instance.arrayNode();

		for (ClimateServiceComment climateServiceComment : climateServiceComments) {
			ObjectNode oneComment = JsonNodeFactory.instance.objectNode();
			oneComment.put("comment_id", climateServiceComment.getCommentId());
			oneComment.put("parent_id", parentId);
			oneComment.put("in_reply_to", climateServiceComment.getInReplyTo());
			oneComment.put("element_id", elementId);
			oneComment.put("created_by", climateServiceComment.getCreatedBy());
			oneComment.put("fullname", climateServiceComment.getFullname());
			oneComment.put("picture", climateServiceComment.getPicture());
			oneComment.put("posted_date",
					timeFormat.format(climateServiceComment.getPostedDate()));
			oneComment.put("text", climateServiceComment.getText());
			oneComment.put("attachments", JsonNodeFactory.instance.arrayNode());
			oneComment.put("childrens",
					getCommentArray(elementId, climateServiceComment.getCommentId()));
			commentArray.add(oneComment);
		}

		return commentArray;
	}

	private void deleteCommentById(Long elementId, Long commentId) {
		List<ClimateServiceComment> climateServiceComments = climateServiceCommentRepository
				.findAllByClimateServiceIdAndParentId(elementId, commentId);
		List<Mention> mentions = mentionRepository
				.findAllMentionByCommentId(commentId);
		List<HashTag> hashTags = hashTagRepository
				.findHashTagsByCommentId(commentId);

		for (Mention mention : mentions) {
			mentionRepository.delete(mention);
		}

		for (HashTag hashTag : hashTags) {
			hashTagRepository.delete(hashTag);
		}

		for (ClimateServiceComment climateServiceComment : climateServiceComments) {
			deleteCommentById(elementId, climateServiceComment.getCommentId());
			climateServiceCommentRepository.delete(climateServiceComment);
		}

		climateServiceCommentRepository.delete(climateServiceCommentRepository.findCommentById(commentId));
	}

	public Result getComment(Long id, String email, String format) {
		System.out.println("GET COMMENT");
		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ObjectNode user = Json.newObject();
		JsonNode json = request().body().asJson();

		// User node
		if (userRepository.getUserIdByEmail(email) == null) {
			user.put("user_id", -1);
			user.put("fullname", "Visitor");
			user.put("is_logged_in", false);
			user.put("is_add_allowed", false);
			user.put("is_edit_allowed", false);
		} else {
			user.put("user_id", userRepository.getUserIdByEmail(email));
			user.put("fullname", userRepository.getUserNameByEmail(email));
			user.put("is_logged_in", true);
			user.put("is_add_allowed", true);
			user.put("is_edit_allowed", true);
		}
		user.put("picture", "/assets/images/user_blank_picture.png");

		// result
		result.put("comments", getCommentArray(id, 0L));
		result.put("total_comment", climateServiceCommentRepository.countComments(id));
		result.put("user", user);

		// response
		response.put("results", result);

		return ok(response.toString());
	}

	private void addHashTags(ClimateServiceComment climateServiceComment) {
		Matcher mat = HASHTAG_PATTERN.matcher(climateServiceComment.getText());

		System.out.println("add hash tags " + climateServiceComment.getText());

		List<HashTag> htags = new ArrayList<HashTag>();
		while (mat.find()) {
			String serviceName = mat.group(1);
			System.out.println("matched hash tag " + serviceName);
			List<ClimateService> services = climateServiceRepository
					.findAllByName(serviceName);
			if (!services.isEmpty()) {
				ClimateService service = services.get(0);
				HashTag htag = new HashTag(climateServiceComment, service, serviceName);
				htags.add(htag);
			}
		}
		hashTagRepository.save(htags);
	}

	public Result postComment() {
		System.out.println("POST COMMENT");

		ObjectNode response = Json.newObject();
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("ClimateServiceComment not saved, expecting Json data");
			return badRequest("ClimateServiceComment not saved, expecting Json data");
		}

		try {
			long parentId = json.findPath("parent_id").asLong();
			String text = checkMention(json.findPath("text").asText());
			String email = json.findPath("email").asText();
			Long createdBy = userRepository.getUserIdByEmail(email);
			String fullname = userRepository.getUserNameByEmail(email);

			long serviceId = json.findPath("climate_service_id").asLong();
			Date postedDate = timeFormat.parse(json.findPath("posted_date")
					.asText());
			String inReplyTo = null;

			// if inside reply
			if (parentId != 0) {
				inReplyTo = userRepository.getUserNameById(parentId);
			}

			ClimateServiceComment climateServiceComment = new ClimateServiceComment(parentId, inReplyTo, serviceId,
					createdBy, fullname,
					"/assets/images/user_blank_picture.png", postedDate, text);
			ClimateServiceComment climateServiceCommentEntry = climateServiceCommentRepository.save(climateServiceComment);
			addHashTags(climateServiceCommentEntry);
			saveMention(json.findPath("text").asText(),
					climateServiceCommentEntry.getCommentId());

			response.put("success", true);
			response.put("comment_id", climateServiceCommentEntry.getCommentId());
			response.put("parent_id", climateServiceCommentEntry.getParentId());
			response.put("created_by", climateServiceCommentEntry.getCreatedBy());
			response.put("fullname", climateServiceCommentEntry.getFullname());
			response.put("picture", climateServiceCommentEntry.getPicture());
			response.put("posted_date",
					timeFormat.format(climateServiceCommentEntry.getPostedDate()));
			response.putArray("childrens");
			response.put("text", climateServiceCommentEntry.getText());
			response.put("is_logged_in", true);
			response.put("is_edit_allowed", true);
			response.put("is_add_allowed", true);
		} catch (ParseException pe) {
			pe.printStackTrace();
			System.out.println("Invalid date format");
			return badRequest(failJson("Invalid date format"));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("ClimateServiceComment not saved");
			return badRequest(failJson("ClimateServiceComment not saved"));
		}

		return ok(response);
	}

	public Result editComment() {
		System.out.println("EDIT COMMENT");

		ObjectNode response = Json.newObject();
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("ClimateServiceComment not updated, expecting Json data");
			return badRequest(failJson("ClimateServiceComment not updated, expecting Json data"));
		}
		System.out.println(json.toString());

		try {
			String text = checkMention(json.findPath("text").asText());
			Long commentId = json.findPath("comment_id").asLong();
			Date postedDate = timeFormat.parse(json.findPath("posted_date")
					.asText());

			ClimateServiceComment climateServiceComment = climateServiceCommentRepository.findCommentById(commentId);
			climateServiceComment.setText(text);
			climateServiceComment.setPostedDate(postedDate);
			ClimateServiceComment climateServiceCommentEntry = climateServiceCommentRepository.save(climateServiceComment);
			saveMention(json.findPath("text").asText(),
					climateServiceCommentEntry.getCommentId());

			response.put("success", true);
			response.put("text", climateServiceCommentEntry.getText());
		} catch (ParseException pe) {
			pe.printStackTrace();
			System.out.println("Invalid date format");
			return badRequest(failJson("Invalid date format"));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("ClimateServiceComment not updated");
			return badRequest(failJson("ClimateServiceComment not updated"));
		}

		return ok(response.toString());
	}

	public Result deleteComment(Long service_id, Long comment_id) {
		System.out.println("DELETE COMMENT");

		ObjectNode response = Json.newObject();

		try {
			deleteCommentById(service_id, comment_id);

			response.put("success", true);
			response.put("total_comment",
					climateServiceCommentRepository.countComments(service_id));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("ClimateServiceComment not deleted");
			return badRequest(failJson("ClimateServiceComment not deleted"));
		}

		return ok(response.toString());
	}

	public Result searchCommentByHashTag(String hashTag) {
		System.out.println("searchCommentByHashTag" + hashTag);
		List<HashTag> htags = hashTagRepository
				.findHashTags("%" + hashTag + "");

		ObjectNode result = Json.newObject();
		ArrayNode commentArray = JsonNodeFactory.instance.arrayNode();
		for (HashTag htag : htags) {
			ClimateServiceComment climateServiceComment = htag.getClimateServiceComment();
			ObjectNode oneComment = JsonNodeFactory.instance.objectNode();
			oneComment.put("comment_id", climateServiceComment.getCommentId());
			oneComment.put("in_reply_to", climateServiceComment.getInReplyTo());
			oneComment.put("created_by", climateServiceComment.getCreatedBy());
			oneComment.put("fullname", climateServiceComment.getFullname());
			oneComment.put("picture", climateServiceComment.getPicture());
			oneComment.put("posted_date",
					timeFormat.format(climateServiceComment.getPostedDate()));
			oneComment.put("text", climateServiceComment.getText());
			oneComment.put("attachments", JsonNodeFactory.instance.arrayNode());
			commentArray.add(oneComment);
		}
		System.out.println(commentArray.toString());
		result.put("comments", commentArray);
		return ok(result);
	}

	public Result getMentions(String email) {
		Long userId = userRepository.getUserIdByEmail(email);
		String username = userRepository.getUserNameByEmail(email);
		List<BigInteger> commentIds = mentionRepository
				.findAllCommentIdByUsername(username);
		ArrayNode commentArray = JsonNodeFactory.instance.arrayNode();
		Long total_comment = 0L;

		ObjectNode response = Json.newObject();
		ObjectNode result = Json.newObject();
		ObjectNode user = Json.newObject();

		// User node
		if (userId == null) {
			user.put("user_id", -1);
			user.put("fullname", "Visitor");
			user.put("is_logged_in", false);
			user.put("is_add_allowed", false);
			user.put("is_edit_allowed", false);
		} else {
			user.put("user_id", userId);
			user.put("fullname", username);
			user.put("is_logged_in", true);
			user.put("is_add_allowed", true);
			user.put("is_edit_allowed", true);
		}
		user.put("picture", "/assets/images/user_blank_picture.png");

		for (BigInteger commentId : commentIds) {
			// Long commentId = ((BigInteger)commentIds[i]).longValue();
			ClimateServiceComment climateServiceComment = climateServiceCommentRepository.findCommentById(commentId
					.longValue());

			ObjectNode oneComment = JsonNodeFactory.instance.objectNode();
			oneComment.put("comment_id", climateServiceComment.getCommentId());
			oneComment.put("parent_id", 0);
			oneComment.put("in_reply_to", climateServiceComment.getInReplyTo());
			oneComment.put("element_id", climateServiceComment.getElementId());
			oneComment.put("created_by", climateServiceComment.getCreatedBy());
			oneComment.put("fullname", climateServiceComment.getFullname());
			oneComment.put("picture", climateServiceComment.getPicture());
			oneComment.put("posted_date",
					timeFormat.format(climateServiceComment.getPostedDate()));
			oneComment.put("text", climateServiceComment.getText());
			oneComment.put("attachments", JsonNodeFactory.instance.arrayNode());
			oneComment.put("childrens", JsonNodeFactory.instance.arrayNode());
			commentArray.add(oneComment);
			total_comment++;
		}

		// result
		result.put("comments", commentArray);
		result.put("total_comment", total_comment);
		result.put("user", user);

		// response
		response.put("results", result);

		return ok(response.toString());
	}
}
