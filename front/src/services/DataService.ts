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

    getLink() {
        return axios.get( "/link?delta=1&id1=1&id2=1" )
    }
}
export default new DataService();
