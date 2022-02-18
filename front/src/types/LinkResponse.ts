type Link = {
    proximity: number
    token_type: string
    value_log_first: string
    value_log_second: string
}

export type LinkResponse = {
    computations: Link[]
    proximity: number
}