//import http from "../http-common"
import {TokensRequest} from "../types/TokensRequest";
import axios from "axios";

class DataService {
    getAll( request: TokensRequest ) {
        return axios.post("/tokens", request );
    }

    getTokenTypes() {
        return axios.get( "/tokentypes" );
    }
}
export default new DataService();
