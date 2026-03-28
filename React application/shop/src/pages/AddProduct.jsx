import { useState } from "react";
import { addProduct } from "../services/productService";
import { useNavigate } from "react-router-dom";

function AddProduct() {
  const navigate = useNavigate();
  const [product, setProduct] = useState({
    sku: "",
    name: "",
    description: "",
    price: 0
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct((p) => ({ ...p, [name]: value }));
  };

  const [errors, setErrors] = useState({});
  const [success, setSuccess] = useState("");

  const validate = () => {
    const errs = {};
    if (!product.skuCode.trim()) errs.skuCode = "SKU code is required";
    if (!product.name.trim()) errs.name = "Name is required";

    if (!product.description.trim()) errs.description = "Description is required";
    if (product.price <= 0) errs.price = "Price must be greater than 0";
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccess("");
    if (!validate()) return;
    try {
      await addProduct(product);
      setSuccess("Product added successfully!");
      // reset form
      setProduct({ skuCode: "", name: "", description: "", price: 0 });
      // optionally navigate after a short delay
      setTimeout(() => navigate("/"), 1500);
    } catch (err) {
      setErrors({ submit: "Failed to add product" });
    }
  };

  return (
    <main className="site px-4 py-8">
      <div className="max-w-3xl mx-auto">
        <div className="card">
          <h1 className="text-2xl font-semibold mb-4">Add Product</h1>
          {success && <div className="text-green-600 mb-4">{success}</div>}
          {errors.submit && <div className="text-red-600 mb-4">{errors.submit}</div>}
          <form onSubmit={handleSubmit} className="space-y-4">

            <div>
              <label className="block text-sm mb-1">Sku code</label>
              <input name="skuCode" className="form-input" onChange={handleChange} value={product.skuCode} />
              {errors.skuCode && <div className="text-red-500 text-xs">{errors.skuCode}</div>}
            </div>

            <div>
              <label className="block text-sm mb-1">Name</label>
              <input name="name" className="form-input" onChange={handleChange} value={product.name} />
              {errors.name && <div className="text-red-500 text-xs">{errors.name}</div>}
            </div>

            <div>
              <label className="block text-sm mb-1">Description</label>
              <textarea name="description" className="form-input h-28" onChange={handleChange} value={product.description} />
              {errors.description && <div className="text-red-500 text-xs">{errors.description}</div>}
            </div>

            <div>
              <label className="block text-sm mb-1">Price</label>
              <input name="price" type="number" className="form-input" value={product.price} onChange={handleChange} />
              {errors.price && <div className="text-red-500 text-xs">{errors.price}</div>}
            </div>

            <div className="flex justify-start">
              <button type="submit" className="btn-primary">
                Add Product
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}

export default AddProduct;