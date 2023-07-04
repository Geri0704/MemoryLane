// Importing modules
const mongoose = require("mongoose");
var crypto = require("crypto");

// Creating Journal schema
const JournalSchema = mongoose.Schema({
  prompt: {
    type: String,
    required: true,
  },
  content: {
    type: String,
    required: true,
  },
  happiness: {
    type: Number,
    required: true,
  },
  date: {
    type: String,
    required: true,
    unique: true,
  },
  userEmail: {
    type: String,
    required: true,
  },
});

// Exporting module to allow it to be imported in other files
const Journal = mongoose.model("Journal", JournalSchema);
module.exports = Journal;
