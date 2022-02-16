import http from "../http-common"
import {TokensRequest} from "../types/TokensRequest";

class DataService {
    getAll( request: TokensRequest ) {
        return http.post("/tokens", request );
    }

    getTokenTypes() {
        return http.get( "/tokentypes" );
    }
}
export default new DataService();
