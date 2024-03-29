require("dotenv").config();
const express = require("express");
const app = require("express")();
const server = require("http").createServer(app);
const io = require("socket.io")(server);
const bodyParser = require("body-parser");
var cors = require("cors");
var multer = require("multer");
var upload = multer({ dest: "uploads/" });
const ffmpegPath = require("@ffmpeg-installer/ffmpeg").path;
const ffmpeg = require("fluent-ffmpeg");
ffmpeg.setFfmpegPath(ffmpegPath);
const mongoose = require("mongoose");
mongoose.connect(
  process.env.MONGO_URL || "mongodb://localhost/ungdungdanentang",
  {
    useUnifiedTopology: true,
    useNewUrlParser: true,
    useFindAndModify: false,
  }
);

const userRoute = require("./src/routes/user.route.js");
const authRoute = require("./src/routes/auth.route.js");
const postRoute = require("./src/routes/post.route.js");
const sixRoute = require("./src/routes/six.route.js");
const sevenRoute = require("./src/routes/seven.route.js");
const eightRoute = require("./src/routes/eight.route.js");
const nineRoute = require("./src/routes/nine.route.js");
const tenRoute = require("./src/routes/ten.route.js");
const elevenRoute = require("./src/routes/eleven.route.js");
const bonusRoute = require("./src/routes/bonus.route.js");

const authMiddleware = require("./src/middlewares/auth.middleware.js");
const { OK } = require("./src/constants/statusCode.constant.js");
const Chat = require("./src/models/chat.model.js");

const port = process.env.PORT || 3000;
app.use(cors());
app.use(bodyParser.json({ limit: "50mb" })); 
app.use(bodyParser.urlencoded({ limit: "50mb", extended: true })); 
app.use(express.json());
var cpUpload = upload.fields([
  { name: "images", maxCount: 4 },
  { name: "video", maxCount: 1 },
  { name: "avatar", maxCount: 1 },
  { name: "avatar[]", maxCount: 1 },
  { name: "cover_image", maxCount: 1},
  { name: "cover_image[]", maxCount: 1 },
]);
app.use(cpUpload);
app.all("/", (req, res) => {
  res.status(200).json({
    code: 1000,
    message: OK,
  });
});

app.post("/fileupload", (req, res) => {
  res.write("File uploaded and moved!");
  return res.end();
});

app.use(authRoute);
app.use(authMiddleware.isAuth, userRoute);
app.use(authMiddleware.isAuth, postRoute);
app.use(authMiddleware.isAuth, sixRoute);
app.use(authMiddleware.isAuth, sevenRoute);
app.use(authMiddleware.isAuth, eightRoute);
app.use(authMiddleware.isAuth, nineRoute);
app.use(authMiddleware.isAuth, tenRoute);
app.use(authMiddleware.isAuth, elevenRoute);
app.use(authMiddleware.isAuth, bonusRoute);

app.all("/test", (req, res)=>{
  res.status(200).json("tra ve cho user");
})
chats = [];
rooms = [];

server.listen(port);
