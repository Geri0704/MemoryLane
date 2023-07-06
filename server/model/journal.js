// Importing modules
const mongoose = require("mongoose");

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
  userEmail: {
    type: String,
    required: true,
  },
  date: {
    type: String,
    required: true,
  },
});

JournalSchema.index({ userEmail: 1, date: 1 }, { unique: true });

// Exporting module to allow it to be imported in other files
const Journal = mongoose.model("Journal", JournalSchema);
module.exports = Journal;
