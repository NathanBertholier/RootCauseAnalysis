type Link = {
    id: number
    proximity: number
}

type Proximity = {
    id: number
    links: Link[]
}

type Log = {
    id: number
    content: string
    datetime: string
}

type Token = {
    name: string
    value: number
    count: number
}

export type ReportResponse = {
    root: Log
    target: Log
    tokens: Token[]
    logs: Log[]
    proximity: Proximity[]
}