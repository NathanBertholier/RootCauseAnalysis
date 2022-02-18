export type TokenModel = {
    token_type: number
    token_value: string
}

export type TokensRequest = {
    init_datetime: string
    end_datetime: string
    id: number
    tokenModel: TokenModel
    rows: number
}