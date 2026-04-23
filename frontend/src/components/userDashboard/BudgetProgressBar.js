function BudgetProgressBar({ budgetProgress }) {
  const { categoryName, budgetAmount, spentAmount, percentage } =
    budgetProgress;

  const getProgressColor = () => {
    if (percentage >= 100) {
      return "#ff6464";
    } else if (percentage >= 80) {
      return "#ffb26e";
    } else {
      return "#53d37d";
    }
  };

  const getStatusText = () => {
    if (percentage >= 100) {
      return "超支";
    } else if (percentage >= 80) {
      return "警告";
    }
    return null;
  };

  const displayPercentage = Math.min(percentage, 100);
  const remaining = budgetAmount - spentAmount;

  return (
    <div className="budget-progress-item">
      <div className="budget-progress-header">
        <span className="budget-category-name">{categoryName}</span>
        {getStatusText() && (
          <span className="budget-status" style={{ color: getProgressColor() }}>
            {getStatusText()}
          </span>
        )}
      </div>
      <div className="budget-progress-details">
        <span className="budget-spent">已花费: {spentAmount.toFixed(2)}</span>
        <span className="budget-total">预算: {budgetAmount.toFixed(2)}</span>
        {remaining < 0 && (
          <span className="budget-overage" style={{ color: "#ff6464" }}>
            超支: {Math.abs(remaining).toFixed(2)}
          </span>
        )}
      </div>
      <div className="budget-progress-bar-container">
        <div
          className="budget-progress-bar"
          style={{
            width: `${displayPercentage}%`,
            backgroundColor: getProgressColor(),
          }}
        />
      </div>
      <div className="budget-progress-percentage">{percentage.toFixed(1)}%</div>
    </div>
  );
}

export default BudgetProgressBar;
