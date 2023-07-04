const jwt = require("jsonwebtoken");

// Secret key used to sign the JWT
const secretKey = "9cd64614-9d1d-4f7b-a7ea-1f7c8d727041";

// Middleware to verify the JWT token
function verifyToken(req, res, next) {
  // Get the token from the request headers
  let token = req.headers.authorization;

  // Check if the token exists
  if (!token) {
    return res.status(401).json({ message: "No token provided." });
  }

  token = token.split(" ")[1];
  // Verify the token
  jwt.verify(token, secretKey, (err, decoded) => {
    if (err) {
      return res.status(403).json({ message: "Failed to authenticate token." });
    }

    // Store the authenticated user's information in the request object
    req.user = decoded;
    next();
  });
}

module.exports = {
  verifyToken,
  secretKey,
};
