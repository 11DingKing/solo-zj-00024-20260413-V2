import { useEffect, useState } from "react";
import UserService from "../services/userService";
import AuthService from "../services/auth.service";

function useExpenseVsIncomeSummary(months) {
    const [data, setData] = useState([]);
    const [isError, setIsError] = useState(false);
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        const getData = async () => {
            const income_response = await UserService.getMonthlySummary(AuthService.getCurrentUser().email).then(
                (response) => {
                    if (response.data.status === "SUCCESS") {
                        generateData(response.data.response)
                    }
                },
                (error) => {
                    setIsError(true)
                }
            )
            setIsLoading(false)
        }

        getData()
    }, [months])

    const generateData = (fetchedData) => {
        const finalData = months.map(({ id, monthName }) => {
            const monthData = fetchedData.find((t) => t.month === id)
            let totalIncome = 0
            let totalExpense = 0
            if (monthData) {
                totalIncome = monthData.totalIncomeInYuan !== undefined && monthData.totalIncomeInYuan !== null
                    ? monthData.totalIncomeInYuan
                    : monthData.total_income
                totalExpense = monthData.totalExpenseInYuan !== undefined && monthData.totalExpenseInYuan !== null
                    ? monthData.totalExpenseInYuan
                    : monthData.total_expense
            }
            return {
                id, monthName,
                totalIncome: Number(totalIncome),
                totalExpense: Number(totalExpense)
            }
        })
        setData(finalData)
    }

    return [data, isLoading, isError]
}

export default useExpenseVsIncomeSummary;