export type TokenType = {
    name: string
}

type Token = {
    token_type: TokenType
    value: string[]
}

export type Log = {
    content: string
    datetime: string
    id: number
    tokens: Token[]
}

export type TokensResponse = {
    log: Log[]
}