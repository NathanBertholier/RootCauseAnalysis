type Link = {
    id: number
    proximity: number
}

type Proximity = {
    id: number
    links: Link[]
}

export type Log = {
    id: number
    content: string
    datetime: string
}

export type MostUsedToken = {
    name: string
    value: number
    count: number
}

export type ReportResponse = {
    root: Log
    target: Log
    tokens: MostUsedToken[]
    logs: Log[]
    proximity: Proximity[]
}