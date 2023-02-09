const dotenv = require("dotenv");
dotenv.config();
const fs = require("fs");
const formidable = require("formidable");
const { getVideoDurationInSeconds } = require("get-video-duration");
const mongoose = require("mongoose");

const Post = require("../models/post.model.js");
const User = require("../models/user.model.js");
const ReportPost = require("../models/report.post.model.js");
const Comment = require("../models/comment.model");

const sameFriendsHelper = require("../helpers/sameFriends.helper.js");

const formidableHelper = require("../helpers/formidable.helper.js");

const statusCode = require("../constants/statusCode.constant.js");
const statusMessage = require("../constants/statusMessage.constant.js");
const setUserInfo = async (req, res) => {
    const {
        username,
        description,
        address,
        city,
        country,
        birthday,
        link,
        songtai,
        dentu,
        hoctai,
        nghenghiep,
        sothich
    } = req.query;
    const {_id}= req.userDataPass;
    try {
        var result = await formidableHelper.parseInfo(req);
        var userData = req.userDataPass;
        userData.avatar = result.avatar?result.avatar.url:userData.avatar;
        userData.cover_image = result.cover_image?result.cover_image.url:userData.cover_image;
        userData.description = description?description:userData.description;
        userData.username = username?username:userData.username;
        userData.address = address?address:userData.address;
        userData.city = city?city:userData.city;
        userData.country = country?country:userData.country;
        userData.link = link?link:userData.link;
        userData.nghenghiep = nghenghiep?nghenghiep:userData.nghenghiep;
        userData.hoctai = hoctai?hoctai:userData.hoctai;
        userData.songtai = songtai?songtai:userData.songtai;
        userData.birthday = birthday?birthday:userData.birthday;
        userData.dentu = dentu?dentu:userData.dentu;
        userData.sothich = sothich?sothich:userData.sothich;
        await userData.save();
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: {
                username: username,
                avatar: result.avatar?result.avatar.url:"",
                cover_image: result.cover_image?result.cover_image.url:"",
                country: country,
                city: city,
                link: link,
                description: description,
                nghenghiep: nghenghiep,
                hoctai: hoctai,
                sothich: sothich,
                songtai: songtai,
                dentu: dentu
            }
        })
    } catch (error) {
        console.log(error)
        if (error.message=="params") {
            return res.status(500).json({
                code: statusCode.PARAMETER_VALUE_IS_INVALID,
                message: statusMessage.PARAMETER_VALUE_IS_INVALID,
            });
        } else {
            return res.status(500).json({
                code: statusCode.UNKNOWN_ERROR,
                message: statusMessage.UNKNOWN_ERROR,
            });
        }

    }
};

const getUserInfo = async (req, res) => {
    const { token, user_id } = req.query;
    const { _id } = req.userDataPass;
    try {
        // nếu tự xem thông tin của mình
        if (user_id == _id || !user_id) {
            console.log("trùng với id của user");
            var userData = await User.findById(_id).populate({
                path: "friends",
                select: "username avatar"
            });
            var listing = userData.friends.length;
            userData.listing = listing;
            return res.status(200).json({
                code: statusCode.OK,
                message: statusMessage.OK,
                data: userData,
            });
        }
        // nếu xem thông tin của người khác
        try{
            var otherUserData = await User.findById(user_id).select(
                "username created description avatar cover_image link address city country friends blockedIds is_blocked birthday"
            ).populate({
                path: "friends",
                select: "username avatar"
            });
        }catch (e) {
            return res.status(500).json({
                code: statusCode.USER_IS_NOT_VALIDATED,
                message: statusMessage.USER_IS_NOT_VALIDATED,
            });
        }
        if (
            !otherUserData ||
            otherUserData.is_blocked ||
            otherUserData.blockedIds.includes(_id)
        ) {
            throw Error("notfound");
        }
        is_friend = req.userDataPass.friends.find(e=>e==user_id)?"1":"0";
        sendRequested = req.userDataPass.sendRequestedFriends.find(e=>e.receiver==user_id)?"1":"0";
        requested = req.userDataPass.requestedFriends.find(e=>e.author==user_id)?"1":"0";
        otherUserData.listing = otherUserData.friends.length;
        var userData = req.userDataPass;
        var result = await sameFriendsHelper.sameFriends(userData.friends, user_id);
        delete otherUserData.blockedIds;
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: otherUserData,
            sameFriends: result.same_friends,
            is_friend: is_friend,
            sendRequested: sendRequested,
            requested:requested

        });
    } catch (error) {
        console.log(error)
        if (error.message == "notfound") {
            return res.status(500).json({
                code: statusCode.USER_IS_NOT_VALIDATED,
                message: statusMessage.USER_IS_NOT_VALIDATED,
            });
        } else {
            return res.status(500).json({
                code: statusCode.UNKNOWN_ERROR,
                message: statusMessage.UNKNOWN_ERROR,
            });
        }
    }
};

const getNotification = async (req, res) => {
    var { index, count } = req.query;
    const {_id}= req.userDataPass;
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
        index=index?index:0;
        count=count?count:20;

        var userData = await User.findById(_id).populate({
            path: "notifications.id",

            // select: "username avatar",
        });
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: userData.notifications.sort((a,b)=>b.id.created-a.id.created).slice(Number(index),Number(index)+Number(count)),
        });
    } catch (error) {
        return res.status(500).json({
            code: statusCode.UNKNOWN_ERROR,
            message: statusMessage.UNKNOWN_ERROR,
        });
    }
};

const setReadNotification = async (req, res) => {
    const { notification_id } = req.query;
    const {_id}= req.userDataPass;
    try {
        var userData = req.userDataPass;
        userData.notifications.map(e=>{
            if (e.id==notification_id) {
                e.read="1";
            }
        });
        await userData.save()
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: userData.notifications,
        });
    } catch (error) {
        return res.status(500).json({
            code: statusCode.UNKNOWN_ERROR,
            message: statusMessage.UNKNOWN_ERROR,
        });
    }
};
const setConversation = async (req, res) => {
    const { partner_id } = req.query;
    const { _id } = req.userDataPass;
    try {
        var partnerData = await User.findById(partner_id);
        var userData = req.userDataPass;
        if (
            !partnerData ||
            partnerData.is_blocked ||
            partnerData.blockedIds.includes(_id)||
            userData.blockedIds.includes(partner_id)
        ) {
            throw Error("blocked or not existed");
        }

        var chatData = await new Chat({
            partner_id: [partner_id, _id],
            is_blocked: null,
            created: Date.now(),
        }).save();
        partnerData.conversations.push(chatData._id);
        await partnerData.save();
        if (_id != partner_id) {
            await User.findByIdAndUpdate(_id, {
                $push: {
                    conversations: chatData._id,
                },
            });
        }
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
        });
    } catch (error) {
        return res.status(500).json({
            code: statusCode.UNKNOWN_ERROR,
            message: statusMessage.UNKNOWN_ERROR,
        });
    }
};

const notSuggest = async (req, res)=>{
    const {user_id}= req.query;
    const {_id}= req.userDataPass;
    var {userDataPass} = req;
    try {
        await User.findByIdAndUpdate(_id,{
            $push:{
                not_suggest: user_id
            }
        });
        res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
        })
    } catch (error) {
        console.log(error);
        return res.status(500).json({
            code: statusCode.UNKNOWN_ERROR,
            message: statusMessage.UNKNOWN_ERROR,
        });
    }


}

const searchUser = async (req, res) => {
    var { keyword, index, count } = req.query;
    const { _id } = req.userDataPass;
    // check params
    try {
        index = index ? index : 0;
        count = count ? count : 20;
        if (!keyword) {
            throw Error("params");
        }
        // var savedSearchList = req.userDataPass.

        // mo ta
        //
        // Ưu tiên đứng đầu danh sách là các kết quả có chứa đủ các từ và đúng thứ tự
        // var postData1 =await Post.find({ described: new RegExp(keyword, "i") });
        // Tiếp theo là các kết quả đủ từ nhưng không đúng thứ tự
        var userData1 =await User.find({$or: [
                { username: new RegExp(keyword, "i") },
                { username: new RegExp(keyword.replace(" ", "|"), "i") }
            ]}).select("username avatar");
        res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: userData1
        })
        // await User.findByIdAndUpdate(_id,{
        //     $pull:{
        //         savedSearch: {
        //             keyword: keyword,
        //         }
        //     }
        // })
        // await User.findByIdAndUpdate(_id,{
        //     $push:{
        //         savedSearch: {
        //             keyword: keyword,
        //             created: Date.now(),
        //         }
        //     }
        // })
    } catch (error) {
        if (error.message == "params") {
            return res.status(500).json({
                code: statusCode.PARAMETER_VALUE_IS_INVALID,
                message: statusMessage.PARAMETER_VALUE_IS_INVALID
            })
        } else if (error.message == "nodata") {
            return res.status(500).json({
                code: statusCode.NO_DATA_OR_END_OF_LIST_DATA,
                message: statusMessage.NO_DATA_OR_END_OF_LIST_DATA
            })
        } else {
            return res.status(500).json({
                code: statusCode.UNKNOWN_ERROR,
                message: statusMessage.UNKNOWN_ERROR
            })
        }
    }
}
const getConversation = async (req, res) => {
    var { partner_id, conversation_id, index, count } = req.query;
    const { _id } = req.jwtDecoded.data;
    try {
        if (conversation_id && conversation_id.length > 1) {
            var chatData = await Chat.findById(conversation_id).populate({
                path: "sender",
                select: "username avatar",
                sort: {
                    created: -1,
                },
            });
            if (!chatData) {
                throw Error("nodata");
            }
            return res.status(200).json({
                code: statusCode.OK,
                message: statusMessage.OK,
                data: {
                    conversation_id: chatData._id,
                    conversation: chatData.conversation,
                    is_blocked: chatData.is_blocked == _id,
                },
            });
        } else if (partner_id && partner_id.length > 1) {
            console.log(_id, partner_id);
            var chatData1 = await Chat.findOne({
                partner_id: {
                    $all: [_id, partner_id],
                },
            });
            if (!chatData1) {
                var partnerData = await User.findById(partner_id);
                var userData = req.userDataPass;
                if (
                    !partnerData ||
                    partnerData.is_blocked ||
                    partnerData.blockedIds.includes(_id) ||
                    userData.blockedIds.includes(partner_id)
                ) {
                    throw Error("blocked or not existed");
                }

                var chatData = await new Chat({
                    partner_id: [partner_id, _id],
                    is_blocked: null,
                    created: Date.now(),
                }).save();
                partnerData.conversations.push(chatData._id);
                await partnerData.save();
                if (_id != partner_id) {
                    await User.findByIdAndUpdate(_id, {
                        $push: {
                            conversations: chatData._id,
                        },
                    });
                }
                return res.status(200).json({
                    code: statusCode.OK,
                    message: statusMessage.OK,
                    data: {
                        conversation_id: chatData._id,
                        conversation: [],
                        is_blocked: false,
                    },
                    /*   server:
                        userData.username + " want to message to " + partnerData.username, */
                });
            }
            return res.status(200).json({
                code: statusCode.OK,
                message: statusMessage.OK,
                data: {
                    conversation_id: chatData1._id,
                    conversation: chatData1.conversation,
                    is_blocked: chatData1.is_blocked == _id,
                },
            });
        } else {
            throw Error("nodata");
        }
    } catch (error) {
        console.log(error);
        if (error.message == "nodata") {
            return res.status(500).json({
                code: statusCode.NO_DATA_OR_END_OF_LIST_DATA,
                message: statusMessage.NO_DATA_OR_END_OF_LIST_DATA,
            });
        }
        return res.status(500).json({
            code: statusCode.UNKNOWN_ERROR,
            message: statusMessage.UNKNOWN_ERROR,
        });
    }
};

const getListConversation = async (req, res) => {
    var { index, count } = req.query;
    const { _id } = req.userDataPass;
    try {
        index = index ? index : 0;
        count = count ? count : 20;

        var userData = await User.findById(_id).populate({
            path: "conversations",
            select: "partner_id created is_blocked conversation",
            sort: {
                created: -1,
            },
            populate: {
                path: "partner_id",
                select: "username avatar",
            },
        });
        console.log(userData.conversations);
        var numNewMessage = 0;
        userData.conversations.forEach((element) => {
            element.conversation =
                element.conversation[element.conversation.length - 1];
            if (element.conversation && element.conversation[0].unread == "1")
                numNewMessage += 1;
            // return element;
        });
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: userData.conversations,
            numNewMessage: numNewMessage,
        });
    } catch (error) {
        console.log(error);
        return res.status(500).json({
            code: statusCode.UNKNOWN_ERROR,
            message: statusMessage.UNKNOWN_ERROR,
        });
    }
};

const setReadMessage = async (req, res) => {
    const { partner_id, conversation_id } = req.query;
    const { _id } = req.userDataPass;
    try {
        var userData = req.userDataPass;
        if (!userData || userData.blockedIds.includes(partner_id)) {
            throw Error("nodata");
        }
        var chatData = await Chat.findById(conversation_id).populate({
            path: "partner_id",
            select: "username avatar",
        });
        if (!chatData) {
            throw Error("notfound");
        }
        chatData.conversation = chatData.conversation.map((element) => {
            element.unread = "0";
            return element;
        });
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: chatData,
        });
    } catch (error) {
        if (error.message == "notfound") {
            return res.status(500).json({
                code: statusCode.POST_IS_NOT_EXISTED,
                message: statusMessage.POST_IS_NOT_EXISTED,
            });
        } else if (error.message == "nodata") {
            return res.status(500).json({
                code: statusCode.NO_DATA_OR_END_OF_LIST_DATA,
                message: statusMessage.NO_DATA_OR_END_OF_LIST_DATA,
            });
        } else {
            return res.status(500).json({
                code: statusCode.UNKNOWN_ERROR,
                message: statusMessage.UNKNOWN_ERROR,
            });
        }
    }
};

const deleteConversation = async (req, res) => {
    const { token, partner_id, conversation_id, message_id } = req.query;
    const { _id } = req.userDataPass;
    try {
        var chatData = await Chat.findByIdAndUpdate(conversation_id, {
            $pull: {
                partner_id: _id,
            },
        });
        if (!chatData) {
            throw Error("nodata");
        }
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
        });
    } catch (error) {
        if (error.message == "nodata") {
            return res.status(500).json({
                code: statusCode.NO_DATA_OR_END_OF_LIST_DATA,
                message: statusMessage.NO_DATA_OR_END_OF_LIST_DATA,
            });
        } else {
            return res.status(500).json({
                code: statusCode.UNKNOWN_ERROR,
                message: statusMessage.UNKNOWN_ERROR,
            });
        }
    }
};

const deleteMessage = async (req, res) => {
    const { token, partner_id, conversation_id, message_id } = req.query;
    const { _id } = req.userDataPass;
    try {
        var chatData = await Chat.findByIdAndUpdate(conversation_id, {
            $pull: {
                conversation: message_id,
            },
        });
        if (!chatData) {
            throw Error("nodata");
        }
        return res.status(200).json({
            code: statusCode.OK,
            message: statusMessage.OK,
            data: {
                conversation: chatData.conversation.slice(
                    Number(index),
                    Number(index) + Number(count)
                ),
                is_blocked: chatData.is_blocked == _id,
            },
        });
    } catch (error) {
        if (error.message == "nodata") {
            return res.status(500).json({
                code: statusCode.NO_DATA_OR_END_OF_LIST_DATA,
                message: statusMessage.NO_DATA_OR_END_OF_LIST_DATA,
            });
        } else {
            return res.status(500).json({
                code: statusCode.UNKNOWN_ERROR,
                message: statusMessage.UNKNOWN_ERROR,
            });
        }
    }
};
module.exports = {
    setUserInfo,
    getUserInfo,
    getNotification,
    setReadNotification,
    setConversation,
    notSuggest,
    searchUser,
    getConversation,
    getListConversation,
    setReadMessage,
    deleteMessage,
    deleteConversation,
};
