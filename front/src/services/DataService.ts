//import http from "../http-common"
import {TokensRequest} from "../types/TokensRequest";
import {LinkRequest} from "../types/LinkRequest";
import axios from "axios";

class DataService {
    getAll( request: TokensRequest ) {
        return axios.post("/tokens", request );
    }

    getTokenTypes() {
        return axios.get( "/tokentypes" );
    }

    getLink( request : LinkRequest ) {
        return axios.get( "/link", request )
    }
}
export default new DataService();
