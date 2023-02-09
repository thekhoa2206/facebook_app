const express = require("express");
const userController = require("../controllers/user.controller.js");
const router = express.Router();

router.get("/user", (req, res) => {// test private page
  console.log(req.jwtDecoded)
  return res.status(200).json("this is /user page");
});
router.post("/logout", userController.logout);
router.post("/change_info_after_signup", userController.changeInfoAfterSignup);
router.post("/change_password", userController.change_password);
router.post("/set_block", userController.setBlock);
router.post("/get_push_settings", userController.getPushSettings);
router.post("/set_push_settings", userController.setPushSettings);
router.post("/check_new_version", userController.checkNewVersion);
router.post("/search", userController.search);
router.post("/get_saved_search", userController.getSavedSearch);
router.post("/del_saved_search", userController.delSavedSearch);


module.exports = router;
