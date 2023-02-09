const express = require("express");
const friendController = require("../controllers/friend.controller.js");
const router = express.Router();

router.post("/set_accept_friend", friendController.setAcceptFriend);
router.post("/get_list_suggested_friends", friendController.getListSuggestedFriends);
router.post("/set_request_friend", friendController.setRequestFriend);
router.post("/get_list_blocks", friendController.getListBlocks);
router.post("/get_requested_friends", friendController.getRequestedFriends);
router.post("/get_list_videos", friendController.getListVideos);
router.post("/get_user_friends", friendController.getUserFriends);

module.exports = router;
