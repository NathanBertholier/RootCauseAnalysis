import axios from "axios";
export default axios.create({
    baseURL: "http://45.155.171.151:8081",
    headers: {
        "Content-type": "application/json"
    },
});
