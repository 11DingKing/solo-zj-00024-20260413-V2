import { useEffect, useState } from 'react';
import UserService from '../../services/userService';
import AuthService from '../../services/auth.service';
import Header from '../../components/utils/header';
import Message from '../../components/utils/message';
import Loading from '../../components/utils/loading';
import Search from '../../components/utils/search';
import usePagination from '../../hooks/usePagination';
import PageInfo from '../../components/utils/pageInfo';
import TransactionList from '../../components/userTransactions/transactionList.js';
import { useLocation } from 'react-router-dom';
import Info from '../../components/utils/Info.js';
import Container from '../../components/utils/Container.js';
import toast, { Toaster } from 'react-hot-toast';


function Transactions() {

    const [userTransactions, setUserTransactions] = useState([]);
    const [isFetching, setIsFetching] = useState(true);
    const [transactionType, setTransactionType] = useState('')
    const [startDate, setStartDate] = useState('')
    const [endDate, setEndDate] = useState('')
    const [isExporting, setIsExporting] = useState(false)
    const location = useLocation();

    const {
        pageSize, pageNumber, noOfPages, sortField, sortDirec, searchKey,
        onNextClick, onPrevClick, setNoOfPages, setNoOfRecords, setSearchKey, getPageInfo
    } = usePagination('date')

    const getTransactions = async () => {
        await UserService.get_transactions(AuthService.getCurrentUser().email, pageNumber,
            pageSize, searchKey, sortField, sortDirec, transactionType).then(
                (response) => {
                    if (response.data.status === "SUCCESS") {
                        setUserTransactions(response.data.response.data)
                        setNoOfPages(response.data.response.totalNoOfPages)
                        setNoOfRecords(response.data.response.totalNoOfRecords)
                        return
                    }
                },
                (error) => {
                    toast.error("Failed to fetch all transactions: Try again later!")
                }
            )
        setIsFetching(false)
    }

    const handleExport = async () => {
        setIsExporting(true)
        try {
            const response = await UserService.export_transactions(
                AuthService.getCurrentUser().email,
                searchKey,
                transactionType,
                startDate || null,
                endDate || null
            )

            const contentDisposition = response.headers['content-disposition']
            let filename = 'transactions.csv'
            if (contentDisposition) {
                const filenameMatch = contentDisposition.match(/filename=([^;]+)/)
                if (filenameMatch) {
                    filename = filenameMatch[1]
                }
            }

            const blob = new Blob([response.data], { type: 'text/csv;charset=utf-8;' })
            const link = document.createElement('a')
            const url = URL.createObjectURL(blob)
            link.setAttribute('href', url)
            link.setAttribute('download', filename)
            link.style.visibility = 'hidden'
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
            URL.revokeObjectURL(url)

            toast.success("导出成功！")
        } catch (error) {
            if (error.response && error.response.data) {
                try {
                    const reader = new FileReader()
                    reader.onload = () => {
                        const errorText = reader.result
                        try {
                            const errorJson = JSON.parse(errorText)
                            toast.error(errorJson.response || "导出失败，请重试！")
                        } catch {
                            toast.error("导出失败，请重试！")
                        }
                    }
                    reader.readAsText(error.response.data)
                } catch {
                    toast.error("导出失败，请重试！")
                }
            } else {
                toast.error("导出失败，请重试！")
            }
        } finally {
            setIsExporting(false)
        }
    }

    useEffect(() => {
        getTransactions()
    }, [pageNumber, searchKey, transactionType, sortDirec, sortField])

    useEffect(() => {
        location.state && toast.success(location.state.text)
        location.state = null
    }, [])

    return (
        <Container activeNavId={1}>
            <Header title="Transactions History" />
            <Toaster/>

            {(userTransactions.length === 0 && isFetching) && <Loading />}
            {(!isFetching) &&
                <>
                    <div className='utils'>
                        <Filter
                            setTransactionType={(val) => setTransactionType(val)}
                        />
                        <div className='page'>
                            <Search
                                onChange={(val) => setSearchKey(val)}
                                placeholder="Search transactions"
                            />
                            <PageInfo
                                info={getPageInfo()}
                                onPrevClick={onPrevClick}
                                onNextClick={onNextClick}
                                pageNumber={pageNumber}
                                noOfPages={noOfPages}
                            />
                        </div>
                    </div>
                    <div className='export-section'>
                        <div className='date-filters'>
                            <div className='date-filter'>
                                <label>开始日期:</label>
                                <input
                                    type='date'
                                    value={startDate}
                                    onChange={(e) => setStartDate(e.target.value)}
                                />
                            </div>
                            <div className='date-filter'>
                                <label>结束日期:</label>
                                <input
                                    type='date'
                                    value={endDate}
                                    onChange={(e) => setEndDate(e.target.value)}
                                />
                            </div>
                        </div>
                        <button
                            className='export-btn'
                            onClick={handleExport}
                            disabled={isExporting}
                        >
                            {isExporting ? '导出中...' : '导出 CSV'}
                        </button>
                    </div>
                    {(userTransactions.length === 0) && <Info text={"No transactions found!"} />}
                    {(userTransactions.length !== 0) && <TransactionList list={userTransactions} />}
                </>
            }
        </Container>
    )
}

export default Transactions;


function Filter({ setTransactionType }) {
    return (
        <select onChange={(e) => setTransactionType(e.target.value)} style={{ margin: '0 15px 0 0' }}>
            <option value="">All</option>
            <option value="expense">Expense</option>
            <option value="income">Income</option>
        </select>
    )
}
