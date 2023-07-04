// Importing modules
const express = require("express");
const router = express.Router();
const jwt = require("jsonwebtoken");
const { secretKey } = require("../auth/utils");

// // Importing Journal Schema
const Journal = require("../model/journal");

router.post("/create", async (req, res, next) => {
  // Creating empty journal object
  let newJournal = new Journal();

  const { prompt, content, happiness } = req.body;
  const { email } = req.user;
  // Initialize newJournal object with request data
  (newJournal.prompt = prompt),
    (newJournal.userEmail = email),
    (newJournal.happiness = happiness),
    (newJournal.date = new Date().toJSON().slice(0, 10)),
    (newJournal.content = content);

  // Save newJournal object to database
  try {
    await newJournal.save();
    return res.status(201).send({
      message: "Journal added successfully.",
    });
  } catch (err) {
    return res.status(400).send({
      message: "Failed to add journal.",
    });
  }
});

router.put("/edit", async (req, res) => {
  // Find journal with requested email

  const { content, happiness } = req.body;
  const { email } = req.user;

  const journal = await Journal.findOne({ userEmail: email, date });
  if (journal === null) {
    return res.status(400).send({
      message: "Journal not found.",
    });
  } else {
    (journal.happiness = happiness), (journal.content = content);

    // Save journal object to database
    try {
      await journal.save();
      return res.status(201).send({
        message: "Journal added successfully.",
      });
    } catch (err) {
      return res.status(400).send({
        message: "Failed to add journal.",
      });
    }
  }
});

// Export module to allow it to be imported in other files
module.exports = router;
