// Importing modules
const express = require("express");
const router = express.Router();
const jwt = require("jsonwebtoken");

// // Importing Journal Schema
const Journal = require("../model/journal");

router.get("/", async (req, res, next) => {
  const { email } = req.user;

  const journals = await Journal.find({
    userEmail: email,
  });
  try {
    return res.status(201).send({
      journals,
    });
  } catch (err) {
    return res.status(400).send({
      message: "Failed to fetch journals.",
    });
  }
});

router.post("/save", async (req, res) => {
  // Find journal with requested email

  const {
    prompt,
    entry,
    happinessRating,
    themes,
    positives,
    negatives,
    workOn,
    date,
  } = req.body;

  const { email } = req.user;

  const journal = await Journal.findOne({
    userEmail: email,
    date: date,
  });
  if (journal === null) {
    // Creating empty journal object
    let newJournal = new Journal();

    const { email } = req.user;
    // Initialize newJournal object with request data
    (newJournal.prompt = prompt),
      (newJournal.userEmail = email),
      (newJournal.happinessRating = happinessRating),
      (newJournal.date = date),
      (newJournal.entry = entry),
      (newJournal.themes = themes),
      (newJournal.positives = positives),
      (newJournal.negatives = negatives),
      (newJournal.workOn = workOn);
    // Save newJournal object to database
    try {
      await newJournal.save();
      return res.status(201).send({
        message: "Journal saved successfully.",
      });
    } catch (err) {
      console.log(err);
      return res.status(400).send({
        message: "Failed to save journal.",
      });
    }
  } else {
    (journal.happinessRating = happinessRating),
      (journal.prompt = prompt),
      (journal.entry = entry),
      (journal.themes = themes),
      (journal.positives = positives),
      (journal.negatives = negatives),
      (journal.workOn = workOn);

    // Save journal object to database
    try {
      await journal.save();
      return res.status(201).send({
        message: "Journal saved successfully.",
      });
    } catch (err) {
      console.log(err);
      return res.status(400).send({
        message: "Failed to save journal.",
      });
    }
  }
});

router.post("/save_multiple", async (req, res) => {
  // Find journal with requested email

  const { journals } = req.body;

  const { email } = req.user;

  var bulkOps = [];

  journals.forEach(
    ({
      prompt,
      entry,
      happinessRating,
      themes,
      positives,
      negatives,
      workOn,
      date,
    }) => {
      let upsertDoc = {
        updateOne: {
          filter: { userEmail: email, date },
          update: {
            $set: {
              prompt,
              entry,
              happinessRating,
              themes,
              positives,
              negatives,
              workOn,
            },
          },
          upsert: true,
        },
      };
      bulkOps.push(upsertDoc);
    }
  );
  try {
    await Journal.collection.bulkWrite(bulkOps);
    return res.status(200).send({
      message: "Sucessfully saved journals.",
    });
  } catch (err) {
    console.log(err);
    return res.status(400).send({
      message: "Failed to save journals.",
    });
  }
});

// Export module to allow it to be imported in other files
module.exports = router;
