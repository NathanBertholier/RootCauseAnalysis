type Link = {
    id: number
    proximity: number
}

export type Log = {
    id: number
    content: string
    datetime: string
}

export type MostUsedToken = {
    name: string
    value: string[]
    count: number
}

type Proximity = {
    id: number
    links: Link[]
}

type Report = {
    rootCause: Log
    target: Log
    tokens: MostUsedToken[]
    logs: Log[]
}

export type ReportResponse = {
    report: Report
    proximity: Proximity[]
}