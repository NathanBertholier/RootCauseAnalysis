type Token = {
    token_type: string
    token_value: string
}

export type Log = {
    content: string
    datetime: string
    id: number
    tokenModels: Token[]
}

export type ResponseData = {
    log: Log
}

export type RequestData = {
    init_datetime: string
    end_datetime: string
    id: number
    tokens: Token[]
    rows: number
}