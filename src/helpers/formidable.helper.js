require("dotenv").config();
const formidable = require("formidable");
const { getVideoDurationInSeconds } = require("get-video-duration");
const statusCode = require("../constants/statusCode.constant");
const cloud = require("./cloud.helper");
let parse = (req, postData) => {
  return new Promise(async (resolve, reject) => {
    var imageList = (req.files&& req.files["images[]"])?req.files["images[]"]:[];
    var videoList = (req.files&& req.files.video)?req.files.video:null;
    var numberOfImages = (req.files&& req.files["images[]"])?req.files["images[]"].length:0;
    var numberOfVideos = (req.files&& req.files.video&&req.files.video[0].mimetype.slice(0,5)=="video")?req.files.video.length:0;
    if(!numberOfImages&&!numberOfVideos){
      return resolve({type: "{avatar: null, cover_image: null}", data: {}})
    }
    if (numberOfImages>4) {
      return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
    }
    if (numberOfVideos>1){
      return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
    }
    if (numberOfImages>0&&numberOfVideos>0){
      return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
    }
    function checkAdult(e) {
      return e.size>4*1024*1024;
    }
    var isTooSize = imageList.find(checkAdult);
    if (isTooSize) {
      return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
    }
    if (videoList&&videoList[0].size<1024*1024&&videoList[0].size>10*1024*1024) {
      return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
    }
    var duration;
    if (videoList) {
      duration = await getVideoDurationInSeconds(videoList[0].path)
    }
    if (duration < 1 || duration > 10) {
      return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
    }
    if (numberOfImages>0) {
      resolve({ type: "image", data: imageList });
    }
    if (numberOfVideos>0) {
      resolve({ type: "video", data: videoList?videoList:{avatar: null, cover_image: null} });
    }

  });
};

let parseOld = (req, postData) => {
  return new Promise((resolve, reject) => {
    const form = new formidable.IncomingForm();
    form.parse(req, (err, fields, files) => {
      var numberOfImages =
        postData && postData.image ? postData.image.length : 0;
      var numberOfVideos =
        postData && postData.video && postData.video.url ? 1 : 0;
      var imageList = [];
      var videoList = [];
      var tempType = "";
      var file = "";
      for (const key in files) {
        if (files.hasOwnProperty(key)) {
          file = files[key];
          tempType = file.type.split("/")[1];
          if (tempType == "jpg" || tempType == "jpeg" || tempType == "png") {
            numberOfImages++;
            if (numberOfImages > 4) {
              return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
            }
            if (file.size > 4 * 1024 * 1024) {
              return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
            }
            imageList.push(file.path);
          } else if (tempType == "mp4" || tempType == "3gp") {
            numberOfVideos++;
            if (numberOfVideos > 2) {
              return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
            }
            if (file.size > 10 * 1024 * 1024) {
              return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
            }
            getVideoDurationInSeconds(file.path)
              .then((duration) => {
                if (duration < 1 || duration > 10) {
                  return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
                }
              })
              .catch((err) => {
                if (err) {
                  return reject(statusCode.UNKNOWN_ERROR);
                }
              });
            videoList.push(file.path);
          }
        }
      }
      if (numberOfVideos == 1 && numberOfImages == 0) {
        resolve({ type: "video", data: videoList });
      } else if (
        numberOfVideos == 0 &&
        numberOfImages <= 4 &&
        numberOfImages > 0
      ) {
        resolve({ type: "image", data: imageList });
      } else if (numberOfImages == 0 && numberOfImages == 0) {
        resolve({ type: "{avatar: null, cover_image: null}", data: {} });
      } else {

        return reject(statusCode.FILE_SIZE_IS_TOO_BIG);
      }
    });
  });
};
let parseInfo = (req) => {
  var files = req.files;
  return new Promise(async (resolve, reject) => {
    if (!files||(!files.avatar&&!files.cover_image)) {
      return resolve({avatar: null, cover_image: null});
    }
    if (
      (files.avatar&& files.avatar[0].size > 1024 * 1024 * 4) ||
      (files.cover_image&& files.cover_image[0].size > 1024 * 1024 * 4)
    ) {
      return  resolve({avatar: null, cover_image: null});
    }
    if ((files.avatar&&files.avatar[0].mimetype.slice(0,5)!="image")||
    (files.cover_image&&files.cover_image[0].mimetype.slice(0,5)!="image")) {

      return  resolve({avatar: null, cover_image: null});
    }
    var resAvatar, resCoverImage; 
    try {
      if (files.avatar) {
        resAvatar = await cloud.upload(files.avatar[0], "image");
      }
      if (files.cover_image) {
        resCoverImage= await cloud.upload(files.cover_image[0], "image");
      }
    } catch (error) {
    }
    
    return resolve({ avatar: resAvatar, cover_image: resCoverImage });
  });
};

module.exports = {
  parse: parse,
  parseInfo: parseInfo,
};
