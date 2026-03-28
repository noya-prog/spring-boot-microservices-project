import { useEffect, useState } from "react";
import { getProducts } from "../services/productService";
import { placeOrder } from "../services/orderService";
import keycloak from "../auth/keycloak";

function Home() {
  const [products, setProducts] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [isAuthenticated, setIsAuthenticated] = useState(!!keycloak.authenticated);

  useEffect(() => {
    getProducts().then(setProducts);
  }, []);

  useEffect(() => {
    setIsAuthenticated(!!keycloak.authenticated);
    const prev = {
      onAuthSuccess: keycloak.onAuthSuccess,
      onAuthLogout: keycloak.onAuthLogout,
      onAuthRefreshSuccess: keycloak.onAuthRefreshSuccess,
      onAuthRefreshError: keycloak.onAuthRefreshError,
    };

    keycloak.onAuthSuccess = () => setIsAuthenticated(true);
    keycloak.onAuthRefreshSuccess = () => setIsAuthenticated(true);
    keycloak.onAuthLogout = () => setIsAuthenticated(false);
    keycloak.onAuthRefreshError = () => setIsAuthenticated(false);

    return () => {
      keycloak.onAuthSuccess = prev.onAuthSuccess;
      keycloak.onAuthLogout = prev.onAuthLogout;
      keycloak.onAuthRefreshSuccess = prev.onAuthRefreshSuccess;
      keycloak.onAuthRefreshError = prev.onAuthRefreshError;
    };
  }, []);

  return (
    <main className="site px-4 py-8">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-4xl font-extrabold text-center mb-8">Products</h1>

        <div className="grid gap-6 md:grid-cols-2">
        {console.log(products)}
          {products.map((product) => (
            <article key={product.id} className="card flex flex-col justify-between">
              <div>
                <h3 className="text-lg font-semibold mb-2">{product.name}</h3>
                <p className="text-sm text-slate-300 mb-2">Price: ${product.price}</p>
                <p className="text-sm text-slate-400">{product.description}</p>
              </div>
              {isAuthenticated && (
              <div className="mt-4 flex items-center justify-end">
                <input
                  type="number"
                  min={1}
                  className="w-20 mr-3 px-2 py-1 bg-slate-900 border border-white text-white rounded"
                  value={quantities[product.id] ?? 1}
                  onChange={(e) =>
                    setQuantities((prev) => ({
                      ...prev,
                      [product.id]: Math.max(1, parseInt(e.target.value) || 1),
                    }))
                  }
                />
                <button
                  className="btn-primary"
                  onClick={async () => {
                    const quantity = quantities[product.id] ?? 1;
                    const userDetails = {
                      email: keycloak.tokenParsed?.email,
                      firstname: keycloak.tokenParsed?.given_name,
                      lastname: keycloak.tokenParsed?.family_name,
                    };
                    try {
                      await placeOrder({
                        skuCode: product.skuCode,
                        quantity,
                        price: product.price,
                        userDetails,
                      });
                      alert("Order placed");
                    } catch (err) {
                      console.error(err);
                      alert("Failed to place order");
                    }
                  }}
                >
                  Order Now
                </button>
              </div>
              )}
            </article>
          ))}
        </div>
      </div>
    </main>
  );
}

export default Home;