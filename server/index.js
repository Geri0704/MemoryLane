const express = require("express");
const mongoose = require("mongoose");
var bodyparser = require("body-parser");
const { secretKey, verifyToken } = require("./auth/utils");
const app = express();
const port = 3000;

// DB
const DATABASE_CONNECTION_STRING =
  "mongodb+srv://mingliang:mingiscool@mingtest.5sbeyk8.mongodb.net/";
mongoose.connect(DATABASE_CONNECTION_STRING);
const database = mongoose.connection;
database.on("error", (error) => {
  console.log(error);
});

database.once("connected", () => {
  console.log("Database Connected");
});

// Using bodyparser to parse json data
app.use(bodyparser.json());

// Importing routes
const user = require("./route/user");

// protected routes
app.use(verifyToken);
const journal = require("./route/journal");

// Use user route when url matches /api/user/
app.use("/user", user);
app.use("/journal", journal);

// Protected route that requires authentication
app.get("/protected", (req, res) => {
  // Access the authenticated user's information
  const { email } = req.user;

  // Return the protected resource
  res.json({ message: `Welcome, ${email}! This is a protected resource.` });
});

app.listen(port, () => {
  console.log(`Example app listening on port ${port}`);
});
