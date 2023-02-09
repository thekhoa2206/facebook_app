const dotenv = require("dotenv");
dotenv.config();

const Post = require("../models/post.model.js");
const User = require("../models/user.model.js");
const ReportPost = require("../models/report.post.model.js");
const Comment = require("../models/comment.model");
const Notification = require("../models/notification.model");
const formidableHelper = require("../helpers/formidable.helper");
const cloudHelper = require("../helpers/cloud.helper.js");

const statusCode = require("./../constants/statusCode.constant.js");
const statusMessage = require("./../constants/statusMessage.constant.js");
const { Mongoose } = require("mongoose");
const deletePostAll = async (req, res)=>{
  var userAll = await User.find({});
  await Promise.all(userAll.map(userData=>{
    return User.findByIdAndUpdate(userData._id, {
      $set:{
        postIds: []
      }
    })
  }))
  await Post.deleteMany({_id:{$ne: null}});
  res.status(200).json({
    message: "drop ok"
  })
}
const addPost = async (req, res) => {
  const { token, described, state, can_edit, status } = req.query;
  const { _id } = req.userDataPass;
  const images = req.files['images'];
  const video = req.files['video'];
  try {
    var newPost;
    var result = await formidableHelper.parse(req);
    if (video) {
      var result2 = await cloudHelper.upload(result.data[0], "video");
      newPost = await new Post({
        described: described,
        state: state,
        status: status,
        video: result2,
        created: Date.now(),
        modified: Date.now(),
        like: 0,
        is_liked: false,
        comment: 0,
        author: _id,
      }).save();

    } else if (images) {
      var result2 = await Promise.all(
          images.map((element) => {
          return cloudHelper.upload(element);
        })
      );
      newPost = await new Post({
        described: described,
        state: state,
        status: status,
        image: result2,
        created: Date.now(),
        modified: Date.now(),
        like: 0,
        is_liked: false,
        comment: 0,
        author: _id,
        keyword: removeAscent(described),
      }).save();

    } else {
      newPost = await new Post({
        described: described,
        state: state,
        status: status,
        created: Date.now(),
        modified: Date.now(),
        like: 0,
        is_liked: false,
        comment: 0,
        author: _id,
        keyword: removeAscent(described),
      }).save();

    }
    var userData= await User.findOneAndUpdate(
      { _id: _id },
      {
        $push: {
          postIds: newPost._id,
        },
      }
    );
    res.status(200).json({
      code: statusCode.OK,
      message: statusMessage.OK,
      data: newPost,
    });
    try {
      var newNotification = await new Notification({
        type: "get post",
        object_id: newPost._id,
        title: userData.username+" đã thêm một bài viết mới",
        avatar: userData.avatar,
        group: "1",
        created: Date.now(),
        read: "0",
        userData: _id,
        postData: newPost._id
      }).save();
    } catch (error) {
      console.log(err)
    }
    
    try {
      await Promise.all(userData.friends.map(async element =>{
        return await User.findByIdAndUpdate(element, {
          $push:{
            notifications: {id: newNotification._id, read: "0"}
          }
        })
      }));
    } catch (error) {
      console.log(error)
    }


  } catch (error) {
    console.log("error")
    if (error == statusCode.FILE_SIZE_IS_TOO_BIG) {
      return res.status(200).json({
        code: statusCode.FILE_SIZE_IS_TOO_BIG,
        message: statusMessage.FILE_SIZE_IS_TOO_BIG,
    
      });
    } else {
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
      });
    }
  }
};
const removeAscent = (str) => {
  if (str === null || str === undefined) return str;
  str = str.toLowerCase();
  str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
  str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
  str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
  str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
  str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
  str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
  str = str.replace(/đ/g, "d");
  return str;
}
const getPost = async (req, res) => {
  const { token, id } = req.query;
  const { _id } = req.userDataPass;
  try {
    if (!id) {
      return res.status(200).json({
        code: statusCode.PARAMETER_IS_NOT_ENOUGHT,
        message: statusMessage.PARAMETER_IS_NOT_ENOUGHT,
      });
    }
    var result = await Post.findOne({ _id: id }).populate({
      path: "author",
      select: "_id username avatar",
          }).populate({
            path: "like_list",
            select:"username avatar"
          }).populate({
            path: "comment_list",
            populate:{
              path: "poster",
              select: "username avatar"
            }
          });
    if (!result || result.is_blocked) {
      throw Error("POST_IS_NOT_EXISTED");
    }
    var resultUser = await User.findOne({ _id: result.author._id });
    if (resultUser.blockedIds.includes(_id)) {
      return res.status(200).json({
        code: statusCode.OK,
        message: statusMessage.OK,
        data: {
          isblocked: 1,
        },
      });
    } else {
      return res.status(200).json({
        code: statusCode.OK,
        message: statusMessage.OK,
        data: result,
      });
    }
    // }
  } catch (error) {
    if (error.message == "POST_IS_NOT_EXISTED") {
      return res.status(200).json({
        code: statusCode.POST_IS_NOT_EXISTED,
        message: statusMessage.POST_IS_NOT_EXISTED,
      });
    } else {
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
      });
    }
  }
};

const editPost = async (req, res) => {
  const {
    id,
    described,
    status,
    state,
    image_del,
    image_sort,
    thumb,
    auto_block,
    auto_accept,
  } = req.body;
  try {
    if (
      !id ||
      (described && described.length > 500) ||
      (image_del && typeof image_del == "object" && image_del.length > 4) ||
      (image_sort && image_sort.length > 4)
    ) {
      throw Error("params");
    }
    const postData = await Post.findOne({ _id: id });
  try {

    const images = req.files['images'];
    const video = req.files['video'];
    var updateData = {};
    if (described) {
      postData.described = described;
      postData.keyword = removeAscent(described);
    }
    if (status) {
      postData.status = status;
    }
    if (state) {
      postData.state = state;
    }
    if (status) {
      postData.status = status;
    }
    if (image_del && image_del.length > 0) {
      postData.image = postData.image.filter((element) => {
        if (image_del.includes(String(element._id))) {
          return false;
        } else {
          return true;
        }
      });
    }
    if (video) {
      cloudHelper
          .upload(video[0])
          .then(async (result2) => {
            updateData.video = result2;
            var editPost = await postData.save();
            return res.status(200).json({
              code: statusCode.OK,
              message: statusMessage.OK,
              data: editPost,
            });
          })
          .catch((err) => {
            throw err;
          });
    } else if (images) {
      Promise.all(
          images.map((element) => {
            return cloudHelper.upload(element);
          })
      ).then(async (result2) => {
        postData.image =
            postData.image && postData.length == 0
                ? result2
                : postData.image.concat(result2);
        var editPost = await postData.save();
        return res.status(200).json({
          code: statusCode.OK,
          message: statusMessage.OK,
          data: editPost,
        });
      });
    } else {
      var editPost = await postData.save();
      return res.status(200).json({
        code: statusCode.OK,
        message: statusMessage.OK,
        data: editPost,
      });
    }
  }catch (e) {
    return res.status(200).json({
      code: statusCode.FILE_SIZE_IS_TOO_BIG,
      message: statusMessage.FILE_SIZE_IS_TOO_BIG,
    });
  }
  } catch (error) {
    console.log(error);
    if (error.message == "params") {
      return res.status(200).json({
        code: statusCode.PARAMETER_VALUE_IS_INVALID,
        message: statusMessage.PARAMETER_VALUE_IS_INVALID,
      });
    } else if (error.message == "FILE_SIZE_IS_TOO_BIG") {
      return res.status(200).json({
        code: statusCode.FILE_SIZE_IS_TOO_BIG,
        message: statusMessage.FILE_SIZE_IS_TOO_BIG,
      });
    } else {
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
      });
    }
  }
};

const deletePost = async (req, res) => {
  const { id } = req.query;
  const { _id } = req.userDataPass;
  try {
    var result = await Post.findOneAndDelete({
      _id: id,
      author: _id,
    });
    if (!result) {
      console.log("Khong tim thay bai viet");
      throw Error("Post is not existed");
    }
    return res.status(200).json({
      code: statusCode.OK,
      message: statusMessage.OK,
    });
  } catch (error) {
    if (error.message == "Post is not existed") {
      return res.status(200).json({
        code: statusCode.POST_IS_NOT_EXISTED,
        message: statusMessage.POST_IS_NOT_EXISTED,
     
      });
    } else {
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
      });
    }
  }
};

const reportPost = async (req, res) => {
  const { id, subject, details } = req.query;
  try {
    var result = await Post.findOne({ _id: id });
    if (!result) {
      throw Error("notfound");
    } else if (result.is_blocked) {
      throw Error("blocked");
    }
    await new ReportPost({
      id: id,
      subject: subject,
      details: details,
    }).save();
    return res.status(200).json({
      code: statusCode.OK,
      message: statusMessage.OK,
    });
  } catch (error) {
    if (error.message == "notfound") {
      return res.status(200).json({
        code: statusCode.POST_IS_NOT_EXISTED,
        message: statusMessage.POST_IS_NOT_EXISTED,
      });
    } else if (error.message == "blocked") {
      return res.status(200).json({
        code: statusCode.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
        message: statusMessage.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
      });
    } else {
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
      });
    }
  }
};

const like = async (req, res) => {
  const { id} = req.query;
  const { _id } = req.userDataPass._id;
  if(id){
    try {
      var result = await Post.findOne({ _id: id });
      if (!result) {
        throw Error("notfound");
      }
      if (result.is_blocked) {
        throw Error("isblocked");
      }
      if (result.like_list.includes(String(_id))) {
        var isLiked = false;
        await Post.findByIdAndUpdate(id, {
          $pull: {
            like_list: _id,
          },
          $set: {
            like: result.like - 1,
          },
        });
        res.status(200).json({
          code: statusCode.OK,
          message: statusMessage.OK,
          data: {
            like: result.like - 1,
            is_liked: isLiked
          },
        });
        
        
      } else {
        var isLiked = true;
        await Post.findByIdAndUpdate(id, {
          $push: {
            like_list: _id,
          },
          $set: {
            like: result.like + 1,
            is_liked: false
          },
        });
        res.status(200).json({
          code: statusCode.OK,
          message: statusMessage.OK,
          data: {
            like: result.like + 1,
            is_liked: isLiked
          },
        });
        try {
          if(result.author==_id){
            throw Error("khong can thong bao cho mk");
          }
          var newNotification = await new Notification({
            type: "get post",
            object_id: id,
            title: req.userDataPass.username+" đã like bài viết cuả bạn",
            avatar: req.userDataPass.avatar,
            group: "1",
            created: Date.now(),
          }).save();
          await User.findByIdAndUpdate(result.author,{
            $push:{
              notifications: {
                id: newNotification._id,
                read: "0"
              }
            }
          })
        } catch (error) {
          console.log(error)
        }
      }
    } catch (error) {
      console.log(error.message);
      if (error.message == "isblocked") {
        return res.status(200).json({
          code: statusCode.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
          message: statusMessage.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
        });
      } else if (error.message == "notfound") {
        return res.status(200).json({
          code: statusCode.POST_IS_NOT_EXISTED,
          message: statusMessage.POST_IS_NOT_EXISTED,
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

const getComment = async (req, res) => {
  var { id, count, index } = req.query;
  const { _id } = req.userDataPass;
  try {
    if (!id) {
      throw Error("params");
    }
    index = index ? index : 0;
    count = count ? count : 20;
    var postData = await Post.findOne({ _id: id }).populate({
      path: "comment_list",
      populate: {
        path: "poster",
        select: "_id username avatar",
      },
    });
    if (!postData) {
      console.log("not found");
      throw Error("notfound");
    } else if (postData.is_blocked) {
      throw Error("blocked");
    } else {
      var authorData = await User.findOne({ _id: postData.author });
      if (authorData.blockedIds.includes(String(_id))) {
        throw Error("authorblock");
      }
      var userData = await User.findOne({ _id: _id });
      if (userData.blockedIds.includes(String(postData.author))) {
        throw Error("userblock");
      }
      var result3 = await Promise.all(
        postData.comment_list.map(async (element) => {
          const authorDataComment = await User.findOne({
            _id: element.poster,
          }).select("blockedIds");
          if (
            (authorDataComment.blockedIds &&
              authorDataComment.blockedIds.includes(String(_id))) ||
            userData.blockedIds.includes(String(element.poster._id))
          ) {
            return null;
          } else {
            return Promise.resolve(element);
          }
        })
      );
      return res.status(200).json({
        code: statusCode.OK,
        message: statusMessage.OK,
        data: result3.slice(Number(index),Number(index)+Number(count) ),
      });
    }
  } catch (err) {
    console.log(err);
    if (err.message == "params") {

      return res.status(200).json({
        code: statusCode.PARAMETER_VALUE_IS_INVALID,
        message: statusMessage.PARAMETER_VALUE_IS_INVALID,
 
      });
    } else if (err.message == "notfound") {
      console.log("notfound");
      return res.status(200).json({
        code: statusCode.POST_IS_NOT_EXISTED,
        message: statusMessage.POST_IS_NOT_EXISTED,
  
      });
    } else if (err.message == "blocked") {
      console.log("post is blocked");
      return res.status(200).json({
        code: statusCode.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
        message: statusMessage.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
      
      });
    } else if (err.message == "authorblock") {
      console.log("authorblock");
      return res.status(200).json({
        code: statusCode.NOT_ACCESS,
        message: statusMessage.NOT_ACCESS,
   
      });
    } else if (err.message == "userblock") {
      console.log("userblock");
      return res.status(200).json({
        code: statusCode.NOT_ACCESS,
        message: statusMessage.NOT_ACCESS,
       
      });
    } else {
      console.log("unknown error");
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
     
      });
    }
  }

};

const setComment = async (req, res) => {
  var { id, comment, index, count } = req.query;
  const { _id } = req.userDataPass;

  try {
    index = index ? index : 0;
    count = count ? count : 20;
    var result = await Post.findOne({ _id: id });
    if (!result) {
      throw Error("notfound");
    }
    if (result.is_blocked) {
      throw Error("action");
    }
    var authorData = await User.findOne({ _id: result.author });
    if (authorData.blockedIds.includes(String(_id))) {
      throw Error("blocked");
    }
    var userData = await User.findOne({ _id: _id });
    if (userData.blockedIds.includes(String(result.author))) {
      throw Error("notaccess");
    }
    var newcomment = new Comment({
      poster: _id,
      comment: comment,
      created: Date.now(),
    });
    result.comment_list.push(newcomment._id);
    result.comment++;
    await result.save();
    await newcomment.save();
    console.log(newcomment);
    var result2 = await Post.findOne({ _id: id }).populate({
      path: "comment_list",
      options: { sort: { created: -1 } },

      populate: {
        path: "poster",
        select: "_id avatar username",
      },
    });

    res.status(200).json({
      code: statusCode.OK,
      message: statusMessage.OK,
      data: result2.comment_list.slice(Number(index),Number(index)+Number(count) ),
    });

    try {
      if(result.author==_id){
        throw Error("khong can thong bao cho minh")
      }
      var newNotification = await new Notification({
        type: "get post",
        object_id: id,
        title: userData.username+" đã comment bài viết cuả bạn",
        avatar: userData.avatar,
        group: "1",
        created: Date.now(),
      }).save();
      await User.findByIdAndUpdate(result.author,{
        $push:{
          notifications: {
            id: newNotification._id,
            read: "0"
          }
        }
      })
    } catch (error) {
      console.log(error)
    }

  } catch (error) {
    console.log(error);
    if (error.message == "params") {
      return res.status(200).json({
        code: statusCode.PARAMETER_VALUE_IS_INVALID,
        message: statusMessage.PARAMETER_VALUE_IS_INVALID,
      });
    } else if (error.message == "notfound") {
      return res.status(200).json({
        code: statusCode.POST_IS_NOT_EXISTED,
        message: statusMessage.POST_IS_NOT_EXISTED,
      });
    } else if (error.message == "action") {
      return res.status(200).json({
        code: statusCode.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
        message: statusMessage.ACTION_HAS_BEEN_DONE_PREVIOUSLY_BY_THIS_USER,
      });
    } else if (error.message == "blocked") {
      return res.status(200).json({
        code: statusCode.NOT_ACCESS,
        message: statusMessage.NOT_ACCESS,
      });
    } else {
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
      });
    }
  }
};
const getListPosts = async (req, res) => {
  var {
    token,
    user_id,
    in_campaign,
    camaign_id,
    latitude,
    longitude,
    last_id,
    index,
    count,
  } = req.query;
  const { _id } = req.userDataPass;
  if(!index || !count){
    return res.status(200).json({
      code: statusCode.PARAMETER_IS_NOT_ENOUGHT,
      message: statusMessage.PARAMETER_IS_NOT_ENOUGHT,
    });
  }
  try{
    index = parseInt(index);
    count = parseInt(count);
  }catch (e) {
    return res.status(200).json({
      code: statusCode.PARAMETER_TYPE_IS_INVALID,
      message: statusMessage.PARAMETER_TYPE_IS_INVALID,
    });
  }
  if(isNaN(index) || isNaN(count)){
    return res.status(200).json({
      code: statusCode.PARAMETER_TYPE_IS_INVALID,
      message: statusMessage.PARAMETER_TYPE_IS_INVALID,
    });
  }
  if(index < 0 || count < 0){
    return res.status(200).json({
      code: statusCode.PARAMETER_VALUE_IS_INVALID,
      message: statusMessage.PARAMETER_VALUE_IS_INVALID,
    });
  }
  try {
    console.log(index, count)
    if(index==null||index=="") index=0;
    if(count==null||count=="") count=20;
    if(user_id){
      var resultData = await User.findById(user_id).populate({

        path: "postIds",
        populate: {
          path: "author",
          select: "username avatar",
          populate: {
            path: "comment_list",
            populate:{
              path: "poster",
              select: "username avatar"
            },
          },
          populate: {
            path: "like_list",
            select: "username avatar"
          },
        },

        options: {
          sort: {
            created: -1,
          },
        },
      });
      resultData.postIds.map(e=>{
        e.is_liked= e.like_list.includes(_id);
      })
      return res.status(200).json({
        code: statusCode.OK,
        message: statusMessage.OK,
        data: resultData.postIds.slice(Number(index),Number(index)+Number(count))
      })
    }


    var result = await User.findById(_id).populate({
      path: "friends",
      select: "postIds",
      populate: {
        path: "postIds",
        populate: {
          path: "author",
          select: "avatar username",
        },
        options: {
          sort: {
            created: -1,
          },
        },
      },
    });
    var postRes = [];
    result.friends.map((e, index) => {
      var temp=e.postIds;
      console.log(temp)

      temp.map(e2=>{
        if(e2.like_list){
          e2.is_liked=e2.like_list.includes(_id);
        }
      })
      postRes = postRes.concat(temp);
    });
    if (postRes.length==0) {
      throw Error("nodata");
    }
    function checkAdult(post) {
      return post._id == last_id;
    }
    var findLastIndex = postRes.findIndex(checkAdult);
    var new_items = 0;
    var newLastIndex;
    if (findLastIndex == -1) {
      new_items = postRes.length;
    } else {
      new_items = findLastIndex;
    }
    return res.status(200).json({
      code: statusCode.OK,
      message: statusMessage.OK,
      data: {
        posts: postRes.slice(Number(index),Number(index)+ Number(count)),
        last_id: postRes[0]._id,
        new_items: findLastIndex,
      },
    });
  } catch (error) {
    if (error.message == "params") {
      return res.status(200).json({
        code: statusCode.PARAMETER_VALUE_IS_INVALID,
        message: statusMessage.PARAMETER_VALUE_IS_INVALID,
      });
    } else if (error.message == "nodata") {
      return res.status(200).json({
        code: statusCode.NO_DATA_OR_END_OF_LIST_DATA,
        message: statusMessage.NO_DATA_OR_END_OF_LIST_DATA,
      });
    } else {
      console.log(error);
      return res.status(200).json({
        code: statusCode.UNKNOWN_ERROR,
        message: statusMessage.UNKNOWN_ERROR,
      });
    }
  }
};

const checkNewItem = async (req, res) => {
  const { last_id, category_id } = req.query;
  const { _id } = req.userDataPass;
  try {
    var result = await User.findById(_id).populate({
      path: "friends",
      select: "postIds",
      populate: {
        path: "postIds",
        options: {
          sort: {
            created: -1,
          },
        },
      },
    });
    var postRes = [];
    result.friends.map((e, index) => {
      postRes = postRes.concat(e.postIds);
    });
    function checkAdult(post) {
      return post._id == last_id;
    }
    var findLastIndex = postRes.findIndex(checkAdult);
    var new_items = 0;
    var newLastIndex;
    if (findLastIndex == -1) {
      new_items = postRes.length;
    } else {
      new_items = findLastIndex;
    }
    return res.status(200).json({
      code: statusCode.OK,
      message: statusMessage.OK,
      data: {
        new_items: new_items,
      },
    });
  } catch (error) {
    if (error.message == "params") {
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
};

module.exports = {
  addPost,
  getPost,
  editPost,
  deletePost,
  reportPost,
  like,
  getComment,
  setComment,
  deletePostAll,
  getListPosts,
  checkNewItem,
};
