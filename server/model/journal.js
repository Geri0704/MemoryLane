// Importing modules
const mongoose = require("mongoose");

// Creating Journal schema
const JournalSchema = mongoose.Schema({
  prompt: {
    type: String,
    required: true,
  },
  entry: {
    type: String,
    required: true,
  },
  happinessRating: {
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
    minlength: 10,
    maxlength: 10,
  },
  themes: {
    type: [String],
    required: false,
  },
  positives: {
    type: [String],
    required: false,
  },
  negatives: {
    type: [String],
    required: false,
  },
  workOn: {
    type: [String],
    required: false,
  },
});

JournalSchema.path("date").validate(function (date) {
  return date?.length === 10;
}, "Journal date must be 10 characters");

JournalSchema.index({ userEmail: 1, date: 1 }, { unique: true });

// Exporting module to allow it to be imported in other files
const Journal = mongoose.model("Journal", JournalSchema);
module.exports = Journal;
