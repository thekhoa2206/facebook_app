const jwtHelper = require("../helpers/jwt.helper.js");
const User = require("../models/user.model.js");
const statusCode = require("../constants/statusCode.constant.js");
const statusMessage = require("../constants/statusMessage.constant.js");

const accessTokenSecret =
  process.env.ACCESS_TOKEN_SECRET || "accessTokenSecret";

let isAuth = async (req, res, next) => {
  const tokenFromClient =
    req.body.token || req.query.token || req.headers["x-access-token"];

  if (tokenFromClient) {
    try {
      const decoded = await jwtHelper.verifyToken(
        tokenFromClient,
        accessTokenSecret
      );
      const result = await User.findOne({
        _id: decoded.data._id,
        phonenumber: decoded.data.phonenumber,
        token: tokenFromClient,
      })
      if (!result) {
        throw Error("Unauthorized. Hacker?")
      }
      else if (result.is_blocked) {
        return res.status(401).json({
          code: statusCode.USER_IS_NOT_VALIDATED,
          message: statusMessage.USER_IS_NOT_VALIDATED,
        })
      }
      req.jwtDecoded = decoded;
      req.userDataPass = result;

      next();
    } catch (error) {
      return res.status(401).json({
        code: statusCode.PARAMETER_VALUE_IS_INVALID,
        message: statusMessage.PARAMETER_VALUE_IS_INVALID,
      });
    }
  } else {
    return res.status(403).json({
      code: statusCode.PARAMETER_IS_NOT_ENOUGHT,
      message: statusMessage.PARAMETER_IS_NOT_ENOUGHT,
    });
  }
};
module.exports = {
  isAuth: isAuth,
};
