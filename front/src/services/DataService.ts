import http from "../http-common"
import { ResponseData, RequestData } from "../types/token.type"

class DataService {
    getAll( request: RequestData ) {
        return http.post<Array<ResponseData>>("/tokens", request );
    }
}
export default new DataService();
