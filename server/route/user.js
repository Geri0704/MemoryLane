// Importing modules
const express = require("express");
const router = express.Router();
const jwt = require("jsonwebtoken");
const { secretKey } = require("../auth/utils");

// // Importing User Schema
const User = require("../model/user");

// User login api
router.post("/login", async (req, res) => {
  // Find user with requested email

  const { email, password } = req.body;
  console.log(email, password);
  const user = await User.findOne({ email });
  if (user === null) {
    return res.status(400).send({
      message: "User not found.",
    });
  } else {
    if (user.validPassword(password)) {
      // If authentication is successful, generate a JWT
      const token = jwt.sign({ email }, secretKey);

      // Send the token back to the client
      res.status(201).json({ token });
    } else {
      return res.status(400).send({
        message: "Wrong Password",
      });
    }
  }
});

// User signup api
router.post("/signup", async (req, res, next) => {
  // Creating empty user object
  let newUser = new User();
  const { name, email, password } = req.body;
  // Initialize newUser object with request data
  (newUser.name = name), (newUser.email = email), (newUser.password = password);

  // Call setPassword function to hash password
  newUser.setPassword(password);

  // Save newUser object to database
  try {
    await newUser.save();
    return res.status(201).send({
      message: "User added successfully.",
    });
  } catch (err) {
    return res.status(400).send({
      message: "Failed to add user.",
    });
  }
});

// Export module to allow it to be imported in other files
module.exports = router;
