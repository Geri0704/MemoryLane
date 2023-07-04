# MemoryLane

### Backend

Make sure you have node installed

Open terminal at `/server` then run `npm install` to install all the npm dependencies

To boot up server run `npm run watch` or `node index.js`. I prefer `npm run watch` to hot-load server on saves

Backend server URL should be at `localhost:3000/`

To view the available routes see `index.js` and follow the routes added.
eg.
`app.use("/user", user);`
and inside users we have
`router.post("/login", async (req, res) => {`

This would mean that the URL to this request is a POST at `localhost:3000/user/login`
