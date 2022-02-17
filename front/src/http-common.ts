import axios from "axios";
export default axios.create({
    baseURL: "http://192.168.4.102:8081",
    headers: {
        "Content-type": "application/json"
    },
});