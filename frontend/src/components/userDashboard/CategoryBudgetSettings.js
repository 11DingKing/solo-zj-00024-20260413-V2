import { useState } from "react";
import { useForm } from "react-hook-form";
import "../../assets/styles/transactionList.css";

function CategoryBudgetSettings({
  categories,
  budgetProgress,
  currentMonth,
  onSaveBudget,
  onDeleteBudget,
}) {
  const { register, handleSubmit, reset, formState, setValue } = useForm();
  const [formToggle, setFormToggle] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  const expenseCategories =
    categories?.filter((cat) => cat.transactionType.transactionTypeId === 1) ||
    [];

  const getBudgetForCategory = (categoryId) => {
    return budgetProgress?.find((bp) => bp.categoryId === categoryId);
  };

  const toggleForm = (e, category = null) => {
    e.preventDefault();
    if (category) {
      setSelectedCategory(category);
      setIsEditing(true);
      const existingBudget = getBudgetForCategory(category.categoryId);
      setValue("amount", existingBudget ? existingBudget.budgetAmount : "");
    } else {
      setSelectedCategory(null);
      setIsEditing(false);
      setValue("amount", "");
      setValue("categoryId", "");
    }
    setFormToggle(!formToggle);
  };

  const onSubmit = (formData) => {
    const categoryId = isEditing
      ? selectedCategory.categoryId
      : parseInt(formData.categoryId);
    onSaveBudget(
      categoryId,
      parseFloat(formData.amount),
      currentMonth.id,
      currentMonth.year,
    );
    setFormToggle(false);
    reset();
  };

  const handleDelete = (categoryId) => {
    if (window.confirm("确定要删除这个分类的预算吗？")) {
      onDeleteBudget(categoryId, currentMonth.id, currentMonth.year);
    }
  };

  const categoriesWithBudget = expenseCategories.filter((cat) =>
    getBudgetForCategory(cat.categoryId),
  );
  const categoriesWithoutBudget = expenseCategories.filter(
    (cat) => !getBudgetForCategory(cat.categoryId),
  );

  return (
    <>
      <div className="chart">
        <div className="chart-top">
          <h2>分类预算</h2>
          {currentMonth.id === new Date().getMonth() + 1 && (
            <button onClick={(e) => toggleForm(e)}>添加预算</button>
          )}
        </div>

        {categoriesWithBudget.length === 0 &&
        categoriesWithoutBudget.length === 0 ? (
          <p style={{ color: "var(--second)", marginTop: "20px" }}>
            暂无支出分类
          </p>
        ) : categoriesWithBudget.length === 0 ? (
          <p style={{ color: "var(--second)", marginTop: "20px" }}>
            暂无设置的预算
          </p>
        ) : (
          <div className="category-budget-list">
            {categoriesWithBudget.map((category) => {
              const budget = getBudgetForCategory(category.categoryId);
              const percentage = budget.percentage;
              const displayPercentage = Math.min(percentage, 100);

              const getProgressColor = () => {
                if (percentage >= 100) return "#ff6464";
                if (percentage >= 80) return "#ffb26e";
                return "#53d37d";
              };

              const getStatusText = () => {
                if (percentage >= 100) return "超支";
                if (percentage >= 80) return "警告";
                return null;
              };

              return (
                <div key={category.categoryId} className="category-budget-item">
                  <div className="category-budget-header">
                    <span className="category-name">
                      {category.categoryName}
                    </span>
                    {getStatusText() && (
                      <span
                        className="budget-status-text"
                        style={{ color: getProgressColor() }}
                      >
                        {getStatusText()}
                      </span>
                    )}
                    {currentMonth.id === new Date().getMonth() + 1 && (
                      <div className="category-budget-actions">
                        <button
                          className="edit-btn"
                          onClick={(e) => toggleForm(e, category)}
                        >
                          编辑
                        </button>
                        <button
                          className="delete-btn"
                          onClick={() => handleDelete(category.categoryId)}
                        >
                          删除
                        </button>
                      </div>
                    )}
                  </div>
                  <div className="category-budget-details">
                    <span>已花费: {budget.spentAmount.toFixed(2)}</span>
                    <span>预算: {budget.budgetAmount.toFixed(2)}</span>
                    <span style={{ color: getProgressColor() }}>
                      {percentage.toFixed(1)}%
                    </span>
                  </div>
                  <div className="category-budget-progress-container">
                    <div
                      className="category-budget-progress"
                      style={{
                        width: `${displayPercentage}%`,
                        backgroundColor: getProgressColor(),
                      }}
                    />
                  </div>
                  {budget.spentAmount > budget.budgetAmount && (
                    <div
                      className="budget-overage-text"
                      style={{ color: "#ff6464" }}
                    >
                      超支:{" "}
                      {(budget.spentAmount - budget.budgetAmount).toFixed(2)}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>

      <div className={formToggle ? "budget-form active" : "budget-form"}>
        <form className="auth-form t-form" onSubmit={handleSubmit(onSubmit)}>
          <h1>{isEditing ? "编辑预算" : "添加预算"}</h1>
          <hr />
          {!isEditing && (
            <div className="input-box">
              <label>选择分类</label>
              <br />
              <select
                {...register("categoryId", {
                  required: "请选择分类!",
                })}
              >
                <option value="">-- 请选择分类 --</option>
                {categoriesWithoutBudget.map((cat) => (
                  <option key={cat.categoryId} value={cat.categoryId}>
                    {cat.categoryName}
                  </option>
                ))}
              </select>
              {formState.errors.categoryId && (
                <small>{formState.errors.categoryId.message}</small>
              )}
            </div>
          )}
          {isEditing && selectedCategory && (
            <div className="input-box">
              <label>分类</label>
              <br />
              <input
                type="text"
                value={selectedCategory.categoryName}
                disabled
                style={{ backgroundColor: "#f0f0f0" }}
              />
            </div>
          )}
          <div className="input-box">
            <label>预算金额</label>
            <br />
            <input
              type="text"
              {...register("amount", {
                required: "金额是必填项!",
                pattern: { value: /^[0-9.]{1,}$/g, message: "无效的金额!" },
              })}
            />
            {formState.errors.amount && (
              <small>{formState.errors.amount.message}</small>
            )}
          </div>
          <div className="t-btn input-box">
            <input type="submit" value="保存" className="button button-fill" />
            <input
              type="button"
              className="button outline"
              value="取消"
              onClick={(e) => toggleForm(e)}
            />
          </div>
        </form>
      </div>
    </>
  );
}

export default CategoryBudgetSettings;
