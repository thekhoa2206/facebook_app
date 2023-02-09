const dotenv = require("dotenv");
dotenv.config();
const md5 = require("md5");

const User = require("../models/user.model.js");

const jwtHelper = require("../helpers/jwt.helper.js");

const statusCode = require("./../constants/statusCode.constant.js");
const statusMessage = require("./../constants/statusMessage.constant.js");
const commonConstant = require("../constants/common.constant.js");


const accessTokenLife = process.env.ACCESS_TOKEN_LIFE || commonConstant.ACCESS_TOKEN_LIFE;
const accessTokenSecret =
  process.env.ACCESS_TOKEN_SECRET || "accessTokenSecret";


function validationPhonenumber(phonenumber) {
  if (
    !phonenumber ||
    phonenumber.length != 10 ||
    phonenumber[0] != "0" ||
    phonenumber.match(/[^0-9]/g)
  ) {
    return 1;
  }
  else {
    return 0;
  }
}
function validationPasword(password, phonenumber) {
  if (
    !password ||
    password.length < 6 ||
    password.length > 10 ||
    password === phonenumber ||
    password.match(/[^a-z|A-Z|0-9]/g)
  ) {
    return 1;
  }
  else {
    return 0;
  }
}


const signup = async (req, res) => {
  const { phonenumber, password, uuid, username } = req.query;
  if(phonenumber && password && uuid){
    try {
      if (validationPhonenumber(phonenumber) ||
          validationPasword(password, phonenumber) ||
          !uuid
      ) {
        throw Error("PARAMETER_VALUE_IS_INVALID");
      } else {
        const userData = await User.findOne({ phonenumber: phonenumber });
        if (!userData) {
         
          const hashedPassword = md5(password);

          const user = new User({
            phonenumber: phonenumber,
            password: hashedPassword,
            username: username,
            active: -1,
          });
          const accessToken = await jwtHelper.generateToken(
              {_id: user._id, phonenumber: user.phonenumber},
              accessTokenSecret,
              accessTokenLife
          );
          user.token = accessToken;
          await user.save();
          return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: {
              id: user._id,
              token: accessToken,
            }
          });
        } else {
          
          return res.status(200).json({
            code: statusCode.USER_EXISTED,
            message: statusMessage.USER_EXISTED,
          });
        }
      }
    } catch (error) {
      if (error.message == "PARAMETER_VALUE_IS_INVALID") {
        return res.status(200).json({
          code: statusCode.PARAMETER_VALUE_IS_INVALID,
          message: statusMessage.PARAMETER_VALUE_IS_INVALID,
        });
      } else {
        return res.status(200).json({
          code: statusCode.UNKNOWN_ERROR,
          message: statusMessage.UNKNOWN_ERROR,
        });
      }
    }
  }else{
    return res.status(200).json({
      code: statusCode.PARAMETER_IS_NOT_ENOUGHT,
      message: statusMessage.PARAMETER_IS_NOT_ENOUGHT,
    });
  }

};
const login = async (req, res) => {
  const { phonenumber, password } = req.query;
  if(phonenumber && password){
    try {
      if (validationPhonenumber(phonenumber) || validationPasword(password)) {
        throw Error("PARAMETER_VALUE_IS_INVALID");
      } else {
        const userData = await User.findOne({ phonenumber: phonenumber });
        if (userData) {
          const hashedPassword = md5(password);
          if (hashedPassword == userData.password) {
            const accessToken = await jwtHelper.generateToken(
                {_id: userData._id, phonenumber: userData.phonenumber},
                accessTokenSecret,
                accessTokenLife
            );
           
            await User.findOneAndUpdate(
                { _id: userData._id },
                {
                  $set: {
                    token: accessToken,
                  },
                });
            return res.status(200).json({
              code: statusCode.OK,
              message: statusMessage.OK,
              data: {
                id: userData._id,
                username: userData.username,
                token: accessToken,
                
                avatar: userData.avatar,
              },
            });
          } else {
            return res.status(200).json({
              code: statusCode.USER_IS_NOT_VALIDATED,
              message: statusMessage.USER_IS_NOT_VALIDATED,
      
            });
          }
        } else {
          res.status(200).json({
            code: statusCode.USER_IS_NOT_VALIDATED,
            message: statusMessage.USER_IS_NOT_VALIDATED,
        
          });
        }
      }
    } catch (error) {
      if (error.message == "PARAMETER_VALUE_IS_INVALID") {
        return res.status(200).json({
          code: statusCode.PARAMETER_VALUE_IS_INVALID,
          message: statusMessage.PARAMETER_VALUE_IS_INVALID,
        });
      } else {
        return res.status(200).json({
          code: statusCode.UNKNOWN_ERROR,
          message: statusMessage.UNKNOWN_ERROR,
        });
      }
    }
  }else{
    return res.status(200).json({
      code: statusCode.PARAMETER_IS_NOT_ENOUGHT,
      message: statusMessage.PARAMETER_IS_NOT_ENOUGHT,
    });
  }
};


const getVerifyCode = async (req, res) => {
  const { phonenumber } = req.query;
  if(!phonenumber){
    return res.status(200).json({
      code: statusCode.PARAMETER_IS_NOT_ENOUGHT,
      message: statusMessage.PARAMETER_IS_NOT_ENOUGHT,
    });
  }
  if (
    !phonenumber ||
    phonenumber.length != 10 ||
    phonenumber[0] != "0" ||
    phonenumber.match(/[^0-9]/g)
  ) {
    return res.status(200).json({
      code: statusCode.PARAMETER_VALUE_IS_INVALID,
      message: statusMessage.PARAMETER_VALUE_IS_INVALID,
    });
  } else {
    
    const userData = await User.findOne({ phonenumber: phonenumber });
    if(userData){
      return res.status(200).json({
        code: statusCode.OK,
        message: statusMessage.OK,
      });
    }else{
      return res.status(200).json({
        code: statusCode.USER_IS_NOT_VALIDATED,
        message: statusMessage.USER_IS_NOT_VALIDATED,
      });
    }
    
  }
};

const checkVerifyCode = async (req, res) => {
  const { phonenumber, code_verify } = req.query;

  if(!phonenumber || !code_verify){
    return res.status(200).json({
      code: statusCode.PARAMETER_IS_NOT_ENOUGHT,
      message: statusMessage.PARAMETER_IS_NOT_ENOUGHT,
    });
  }
  if (
    !phonenumber ||
    phonenumber.length != 10 ||
    phonenumber[0] != "0" ||
    phonenumber.match(/[^0-9]/g)
  ) {
    return res.status(200).json({
      code: statusCode.PARAMETER_VALUE_IS_INVALID,
      message: statusMessage.PARAMETER_VALUE_IS_INVALID,
    });
  } else {
    
    const userData = await User.findOne({ phonenumber: phonenumber });
    if (userData) {
      const accessToken = await jwtHelper.generateToken(
        {_id: userData._id, phonenumber: userData.phonenumber},
        accessTokenSecret,
        accessTokenLife
      );
      
      await User.findOneAndUpdate(
        { _id: userData._id },
        {
          $set: {
            token: accessToken,
          },
        });
        return res.status(200).json({
          code: statusCode.OK,
          message: statusMessage.OK,
          data: {
            token: accessToken,
            id: userData._id,
          },
        });
      }else{
        return res.status(200).json({
          code: statusCode.USER_IS_NOT_VALIDATED,
          message: statusMessage.USER_IS_NOT_VALIDATED,
        });
      }
    
  }
};


module.exports = {
  login,
  signup,
  getVerifyCode,
  checkVerifyCode,
};
