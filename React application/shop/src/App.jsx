import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import AddProduct from "./pages/AddProduct";
import Navbar from "./components/Navbar";
import keycloak from "./auth/keycloak";
function App() {
  const isAuth = keycloak?.authenticated;

  return (
    <BrowserRouter>
      {/* pass instance from auth to be used in navbar */}
      <Navbar keycloak={keycloak} />
      <Routes>
        <Route path="/" element={<Home />} />
        {isAuth && <Route path="/add-product" element={<AddProduct />} />}
        {!isAuth && (
          <Route
            path="/add-product"
            element={<Home />} // redirect guest back to home or show message
          />
        )}
      </Routes>
    </BrowserRouter>
  );
}

export default App;