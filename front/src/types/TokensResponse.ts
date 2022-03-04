export type TokenType = {
    id: number
    name: string
}

type Token = {
    token_type: TokenType
    value: string[]
}

export type Log = {
    //rawLog: RawLog
    datetime: string
    id: number
    tokens: Token[]
}

type RawLog = {
    id: number,
    log: string
}

export type TokensResponse = {
    log: Log[]
}