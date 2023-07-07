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

### Connection to backend

Check `build.gradle`. The `BASE_URL` should point to IP address `10.0.2.2` which is an alias for `127.0.0.1` or `localhost` when testing on emulator.

For @ianeen or anyone with an actual Android device. LMAO!!!🤣😂🤣 This is gonna be painful! This is what I had to do.

1. Make sure both devices are on the same WiFi network
2. Go to WiFi settings and check the IPv4 address of your WiFi
3. Enter that IP address into the `BASE_URL` variable
4. Cross your fingers and pray that your Android phone connects to your PC/laptop's local server
5. It works!
