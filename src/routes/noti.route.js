const express = require("express");
const notiController = require("../controllers/noti.controller.js");
const router = express.Router();

router.post("/get_user_info", notiController.getUserInfo);
router.post("/set_user_info", notiController.setUserInfo);

router.post("/get_notification", notiController.getNotification);
router.post("/set_read_notification", notiController.setReadNotification);

router.post("/set_conversation", notiController.setConversation);
router.post("/not_suggest", notiController.notSuggest);
router.post("/search_user", notiController.searchUser);

router.post("/get_conversation", notiController.getConversation);
router.post("/get_list_conversation", notiController.getListConversation);
router.post("/set_read_message", notiController.setReadMessage);
router.post("/delete_message", notiController.deleteMessage);
router.post("/delete_conversation", notiController.deleteConversation);
module.exports = router;
